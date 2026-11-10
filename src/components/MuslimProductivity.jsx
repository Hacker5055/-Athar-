import React, { useState } from 'react';
import { Clock, CheckSquare, Bell, MapPin, VolumeX, Bluetooth, Smartphone, Plus, Trash2, CheckCircle2, ShieldAlert, Sparkles, Navigation } from 'lucide-react';

export default function MuslimProductivity() {
  const [activeTab, setActiveTab] = useState('planner'); // 'planner', 'geofence', 'contextAthkar'

  // Salah Centric Planner Tasks
  const [tasks, setTasks] = useState([
    { id: 1, text: 'أذكار الصباح والورد القرآني (جزء عم)', period: 'ما بين الفجر والشروق', completed: true },
    { id: 2, text: 'التركيز على المهام المهنية العميقة', period: 'ما بين الشروق والظهر', completed: false },
    { id: 3, text: 'قراءة تفسير وتدبر سورة النور وقيلولة سريعة', period: 'ما بين الظهر والعصر', completed: false },
    { id: 4, text: 'رياضة مسائية وصلة رحم عائلية', period: 'ما بين العصر والمغرب', completed: false },
    { id: 5, text: 'تطوير الذات ومراجعة ورد الحفظ', period: 'ما بعد العشاء', completed: false },
  ]);

  const [newTaskText, setNewTaskText] = useState('');
  const [selectedPeriod, setSelectedPeriod] = useState('ما بين الفجر والشروق');

  // Geofencing Simulator State
  const [isInsideMosque, setIsInsideMosque] = useState(false);
  const [selectedMosqueZone, setSelectedMosqueZone] = useState('جامع الراجحي الكبير');
  const [silentTriggerType, setSilentTriggerType] = useState('gps'); // 'gps', 'iqamaTime'

  // Smart Context Athkar State
  const [simulatedSensor, setSimulatedSensor] = useState('none'); // 'bluetooth', 'speed', 'mall'

  const addTask = () => {
    if (!newTaskText.trim()) return;
    setTasks([...tasks, { id: Date.now(), text: newTaskText, period: selectedPeriod, completed: false }]);
    setNewTaskText('');
  };

  const toggleTask = (id) => {
    setTasks(tasks.map(t => t.id === id ? { ...t, completed: !t.completed } : t));
  };

  const deleteTask = (id) => {
    setTasks(tasks.filter(t => t.id !== id));
  };

  const periods = [
    'ما بين الفجر والشروق',
    'ما بين الشروق والظهر',
    'ما بين الظهر والعصر',
    'ما بين العصر والمغرب',
    'ما بين المغرب والعشاء',
    'ما بعد العشاء'
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-blue-900/80 via-emerald-800/50 to-slate-900 border border-blue-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-blue-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold mb-3 border border-blue-500/30">
              <Clock className="w-3.5 h-3.5" />
              الإنتاجية وتنظيم الحياة
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">إنتاجية المسلم ذكية بالكامل</h2>
            <p className="text-slate-300 text-sm mt-1">
              منظم اليوم المعتمد على مواقيت الصلاة، الوضع الصامت الذكي في المسجد، والأذكار المعتمدة على المستشعرات.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('planner')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'planner' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              منظم الصلاة
            </button>
            <button
              onClick={() => setActiveTab('geofence')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'geofence' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الوضع الصامت
            </button>
            <button
              onClick={() => setActiveTab('contextAthkar')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'contextAthkar' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الأذكار الذكية
            </button>
          </div>
        </div>
      </div>

      {/* TAB 1: SALAH CENTRIC PLANNER */}
      {activeTab === 'planner' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <CheckSquare className="w-5 h-5 text-blue-400" />
                منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                ربط مهام اليوم بفترات الصلوات الخمس بدلاً من الساعات الجافة، مع تحديث الجداول تلقائياً بحسب تبدل المواقيت اليومية.
              </p>
            </div>
            <div className="flex items-center gap-2 bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800 text-xs text-emerald-400 font-medium">
              <span>الفجر: 04:45 ص</span> | <span>الظهر: 12:15 م</span> | <span>العصر: 03:30 م</span> | <span>المغرب: 06:10 م</span>
            </div>
          </div>

          {/* Add Task Control */}
          <div className="flex flex-col md:flex-row gap-3">
            <input
              type="text"
              value={newTaskText}
              onChange={(e) => setNewTaskText(e.target.value)}
              placeholder="إضافة مهمة جديدة..."
              className="flex-1 bg-slate-950 border border-slate-700 rounded-xl px-4 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-blue-500"
            />
            <select
              value={selectedPeriod}
              onChange={(e) => setSelectedPeriod(e.target.value)}
              className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-xl px-3 py-2.5 focus:outline-none focus:border-blue-500"
            >
              {periods.map((p, i) => (
                <option key={i} value={p}>{p}</option>
              ))}
            </select>
            <button
              onClick={addTask}
              className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-xl shadow-md transition-all flex items-center justify-center gap-1.5"
            >
              <Plus className="w-4 h-4" />
              إضافة مهمة
            </button>
          </div>

          {/* Task Groups */}
          <div className="grid md:grid-cols-2 gap-4">
            {periods.map((period, pIdx) => {
              const periodTasks = tasks.filter(t => t.period === period);
              if (periodTasks.length === 0) return null;

              return (
                <div key={pIdx} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-3">
                  <h4 className="text-xs font-bold text-blue-300 border-b border-slate-800/80 pb-2 flex items-center justify-between">
                    <span>{period}</span>
                    <span className="text-slate-500 font-normal">({periodTasks.length} مهام)</span>
                  </h4>
                  <div className="space-y-2">
                    {periodTasks.map(task => (
                      <div
                        key={task.id}
                        className={`flex items-center justify-between p-2.5 rounded-lg border transition-all ${
                          task.completed
                            ? 'bg-slate-900/50 border-slate-800/60 text-slate-500 line-through'
                            : 'bg-slate-900 border-slate-700 text-slate-200'
                        }`}
                      >
                        <div
                          onClick={() => toggleTask(task.id)}
                          className="flex items-center gap-2.5 cursor-pointer flex-1"
                        >
                          <CheckCircle2 className={`w-4 h-4 ${task.completed ? 'text-emerald-500' : 'text-slate-600'}`} />
                          <span className="text-xs font-medium">{task.text}</span>
                        </div>
                        <button
                          onClick={() => deleteTask(task.id)}
                          className="text-slate-500 hover:text-red-400 p-1"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* TAB 2: GEOFENCING SILENT MODE */}
      {activeTab === 'geofence' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-3">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <MapPin className="w-5 h-5 text-blue-400" />
              الوضع الصامت التلقائي في المسجد (Geofencing & Mosque Mode)
            </h3>
            <p className="text-slate-400 text-xs">
              محاكاة التعرف على دخول المستخدم إلى نطاق المسجد الجغرافي أو وقت إقامة الصلاة لكتم الهاتف صوناً لحرمة الصلاة.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6 items-center">
            <div className="bg-slate-950 p-6 rounded-2xl border border-slate-800 text-center space-y-4 shadow-xl">
              <div className="inline-flex p-4 rounded-full bg-slate-900 border border-slate-800">
                <VolumeX className={`w-12 h-12 transition-colors ${isInsideMosque ? 'text-emerald-400' : 'text-slate-600'}`} />
              </div>
              <div>
                <h4 className="text-lg font-bold text-white">حالة وضع الهاتف الحالية</h4>
                <p className={`text-xs mt-1 font-semibold ${isInsideMosque ? 'text-emerald-400' : 'text-slate-400'}`}>
                  {isInsideMosque ? `الوضع الصامت مفعّل (أنت الآن داخل نطاق ${selectedMosqueZone})` : 'الوضع العادي (خارج نطاق المساجد)'}
                </p>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => setIsInsideMosque(!isInsideMosque)}
                  className={`flex-1 py-3 px-4 rounded-xl text-xs font-bold transition-all shadow-md ${
                    isInsideMosque
                      ? 'bg-amber-600 hover:bg-amber-500 text-white'
                      : 'bg-emerald-600 hover:bg-emerald-500 text-white'
                  }`}
                >
                  {isInsideMosque ? 'محاكاة الخروج من المسجد' : 'محاكاة دخول نطاق المسجد'}
                </button>
              </div>

              <p className="text-[11px] text-slate-500">
                الحالة: {isInsideMosque ? 'مكتوم ومجدول للعودة بعد انتهاء الصلاة بـ 20 دقيقة' : 'نغمات الرنين والإشعارات نشطة'}
              </p>
            </div>

            <div className="space-y-3 bg-slate-950/60 p-5 rounded-2xl border border-slate-800 text-xs">
              <h5 className="font-bold text-blue-300 flex items-center gap-1.5">
                ⚙️ معايير تفعيل الصامت في تطبيق أثر:
              </h5>

              <div>
                <label className="text-slate-400 block mb-1">المسجد المحدد للاختبار:</label>
                <select
                  value={selectedMosqueZone}
                  onChange={(e) => setSelectedMosqueZone(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-200 rounded-lg p-2 text-xs"
                >
                  <option value="جامع الراجحي الكبير">جامع الراجحي الكبير (نصف قطر 60م)</option>
                  <option value="مسجد التوحيد">مسجد التوحيد (نصف قطر 40م)</option>
                  <option value="جامع الملك فهد">جامع الملك فهد (نصف قطر 75م)</option>
                </select>
              </div>

              <div className="flex items-center justify-between p-2.5 bg-slate-900 rounded-lg">
                <span className="text-slate-300">مسافة تفعيل النطاق:</span>
                <span className="text-blue-400 font-bold">50 - 75 متراً حول المسجد</span>
              </div>
              <div className="flex items-center justify-between p-2.5 bg-slate-900 rounded-lg">
                <span className="text-slate-300">إعادة الصوت التلقائي:</span>
                <span className="text-emerald-400 font-bold">بعد 25 دقيقة من الأذان</span>
              </div>

              <div className="p-2.5 bg-amber-950/30 rounded-lg border border-amber-500/20 text-amber-300 text-[11px] leading-relaxed">
                <span className="font-bold">⚠️ ملاحظة هندسية:</span> بسبب قيود iOS التامة على وضع الصامت التلقائي، يُرسل أثر إشعاراً تفاعلياً (Interactive Notification) بضغطة زر واحدة لتفعيل وضع التركيز (Focus Mode).
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: CONTEXT ATHKAR */}
      {activeTab === 'contextAthkar' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-3">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <Bell className="w-5 h-5 text-blue-400" />
              أذكار ذكية مرتبطة بالسياق والمستشعرات
            </h3>
            <p className="text-slate-400 text-xs">
              بدلاً من الإشعارات العشوائية، تظهر الأذكار بناءً على حالة الجهاز (بلوتوث السيارة، سرعة السفر، أو الدخول للمجمعات).
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-3">
            <button
              onClick={() => setSimulatedSensor('bluetooth')}
              className={`p-4 rounded-xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'bluetooth'
                  ? 'bg-blue-950/80 border-blue-500 text-white'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Bluetooth className="w-5 h-5 text-blue-400" />
                <span className="text-[10px] bg-blue-500/20 text-blue-300 px-2 py-0.5 rounded">بلوتوث</span>
              </div>
              <h4 className="font-bold text-xs text-slate-200">اتصال ببلوتوث السيارة</h4>
              <p className="text-[11px] text-slate-400">تنبيه دعاء الركوب والسفر تلقائياً عند التشغيل.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('speed')}
              className={`p-4 rounded-xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'speed'
                  ? 'bg-blue-950/80 border-blue-500 text-white'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Smartphone className="w-5 h-5 text-amber-400" />
                <span className="text-[10px] bg-amber-500/20 text-amber-300 px-2 py-0.5 rounded">GPS / سرعة</span>
              </div>
              <h4 className="font-bold text-xs text-slate-200">التحرك بسرعة طريق سريع (&gt;80 كم/س)</h4>
              <p className="text-[11px] text-slate-400">إظهار أذكار السفر واستوداع النفس والأهل.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('mall')}
              className={`p-4 rounded-xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'mall'
                  ? 'bg-blue-950/80 border-blue-500 text-white'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <MapPin className="w-5 h-5 text-emerald-400" />
                <span className="text-[10px] bg-emerald-500/20 text-emerald-300 px-2 py-0.5 rounded">موقع</span>
              </div>
              <h4 className="font-bold text-xs text-slate-200">التواجد في المولات والمراكز</h4>
              <p className="text-[11px] text-slate-400">تذكير بدعاء دخول السوق ونيل مليون حسنة.</p>
            </button>
          </div>

          {/* Sensor Notification Box */}
          {simulatedSensor !== 'none' && (
            <div className="p-5 bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 border border-blue-500/40 rounded-xl space-y-3 animate-fade-in shadow-xl">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-blue-400 flex items-center gap-1.5">
                  <Bell className="w-4 h-4 animate-bounce" />
                  إشعار الأذكار الذكي للحدث الحالي:
                </span>
                <span className="text-[10px] text-slate-400 bg-slate-800 px-2 py-0.5 rounded">مُكتشف بالمستشعر</span>
              </div>

              {simulatedSensor === 'bluetooth' && (
                <div>
                  <h5 className="text-base font-serif font-bold text-emerald-300 leading-relaxed">
                    "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ"
                  </h5>
                  <p className="text-xs text-slate-400 mt-1">دعاء الركوب - تم تفعيله عند الاقتران بنظام سيارتك.</p>
                </div>
              )}

              {simulatedSensor === 'speed' && (
                <div>
                  <h5 className="text-base font-serif font-bold text-amber-300 leading-relaxed">
                    "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، وَمِنَ الْعَمَلِ مَا تَرْضَى، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا"
                  </h5>
                  <p className="text-xs text-slate-400 mt-1">دعاء السفر - تم تفعيله عند رصد السرعة العالية على الطريق السريع.</p>
                </div>
              )}

              {simulatedSensor === 'mall' && (
                <div>
                  <h5 className="text-base font-serif font-bold text-teal-300 leading-relaxed">
                    "لا إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لا يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ"
                  </h5>
                  <p className="text-xs text-slate-400 mt-1">دعاء دخول السوق - قال النبي ﷺ: كُتب له ألف ألف حسنة ومُحي عنه ألف ألف سيئة.</p>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
