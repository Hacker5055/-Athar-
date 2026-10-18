package com.athar.athar.qibla

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.toDegrees

/// قراءة اتجاه الهاتف من المستشعرات.
///
/// ⚠️ `TYPE_ROTATION_VECTOR` أولًا: دمج النظام للجيروسكوب والبوصلة
/// أدقّ من جمع القراءات يدويًا، ويتكفّل بالتنعيم. وإن غاب — أجهزة
/// قديمة رخيصة — نعود إلى التسارع + المغناطيسية.
class DeviceCompass(context: Context) : SensorEventListener {

    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationVector = manager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = manager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)
    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    private val _azimuth = MutableStateFlow<Float?>(null)
    val azimuth: StateFlow<Float?> = _azimuth.asStateFlow()

    private val _accuracy = MutableStateFlow(SensorManager.SENSOR_STATUS_UNRELIABLE)
    val accuracy: StateFlow<Int> = _accuracy.asStateFlow()

    /// ⚠️ بلا مستشعر لا بوصلة، والسكوت عن ذلك يوهم المستخدم أن العرض
    /// الحسابي كافٍ. نُخبر الشاشة لتبيّن الأمر.
    val isAvailable: Boolean =
        rotationVector != null || (accelerometer != null && magnetometer != null)

    @Volatile
    private var declination = 0f

    /// ⚠️ الانحراف المغناطيسي يختلف باختلاف المكان **والزمان**، ويصل
    /// إلى عشرين درجة. نحدّثه مع كل تغيير للموقع.
    fun setLocation(latitude: Double, longitude: Double) {
        declination = GeomagneticField(
            latitude.toFloat(),
            longitude.toFloat(),
            0f,
            System.currentTimeMillis(),
        ).getDeclination()
    }

    fun start() {
        val hardware = manager ?: return
        if (rotationVector != null) {
            hardware.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_UI)
            return
        }
        if (accelerometer != null && magnetometer != null) {
            hardware.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            hardware.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        manager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                publish()
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravity, 0, 3)
                if (geomagnetic.any { it != 0f }) publishFromRaw()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                if (gravity.any { it != 0f }) publishFromRaw()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy.value = accuracy
    }

    private fun publishFromRaw() {
        // ترجع false إن كانت قراءة التسارع غير صالحة (الهاتف في سقوط حرّ)
        if (!SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) return
        publish()
    }

    /// `getOrientation` تعطي الاتجاه من الشمال **المغناطيسي**، فنضيف
    /// الانحراف لتصير من الشمال الجغرافي حيث تُقاس زاوية القبلة.
    private fun publish() {
        SensorManager.getOrientation(rotationMatrix, orientation)
        val magnetic = QiblaMath.normalize(toDegrees(orientation[0].toDouble()))
        _azimuth.value = QiblaMath.trueAzimuth(magnetic, declination.toDouble()).toFloat()
    }
}
