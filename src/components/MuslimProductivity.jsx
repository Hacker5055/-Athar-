import React, { useState } from 'react';
import {
  Clock,
  CheckSquare,
  Bell,
  MapPin,
  VolumeX,
  Volume2,
  Bluetooth,
  Smartphone,
  Plus,
  Trash2,
  CheckCircle2,
  AlertCircle,
  Navigation,
  Compass,
  Zap,
  Info
} from 'lucide-react';

export default function MuslimProductivity() {
  const [activeTab, setActiveTab] = useState('planner'); // 'planner', 'geofence', 'contextAthkar'

  // Salah Centric Planner Tasks
  const [tasks, setTasks] = useState([
    { id: 1, text: 'أذكار الصباح وتلاوة ورد جزء عم', period: 'ما بين الفجر والشروق', completed: true },
    { id: 2, text: 'التركيز على المهام الذهنية والعمل الأساسي', period: 'ما بين الشروق والظهر', completed: false },
    { id: 3, text: 'صلاة الظهر وقراءة تفسير آية النور وقيلولة قصيرة', period: 'ما بين الظهر والعصر', completed: false },
    { id: 4, text: 'جلسة عائلية وممارسة رياضة المشي', period: 'ما بين العصر والمغرب', completed: false },
    { id: 5, text: 'مراجعة حفظ السور بنظام التكرار المتباعد', period: 'ما بين المغرب والعشاء', completed: false },
    { id: 6, text: 'أذكار النوم والمحاسبة اليومية', period: 'ما بعد العشاء', completed: false },
  ]);

  const [newTaskText, setNewTaskText] = useState('');
  const [selectedPeriod, setSelectedPeriod] = useState('ما بين الفجر والشروق');

  // Geofencing Simulator State
  const [isInsideMosque, setIsInsideMosque] = useState(false);
  const [selectedMosqueName, setSelectedMosqueName] = useState('جامع الراجحي الكبير');
  const [autoRestoreMinutes, setAutoRestoreMinutes] = useState(20);
  const [silenceLog, setSilenceLog] = useState([
    { time: '12:18 م', event: 'تم كتم الصوت تلقائياً عند دخول جامع الراجحي (صلاة الظهر)' },
    { time: '12:38 م', event: 'تمت استعادة نمط الصوت الطبيعي تلقائياً بعد انقضاء 20 دقيقة' }
  ]);

  // Smart Context Athkar State
  const [simulatedSensor, setSimulatedSensor] = useState('bluetooth'); // 'bluetooth', 'speed', 'mall', 'home'

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

  const handleToggleMosqueGeofence = () => {
    const nextState = !isInsideMosque;
    setIsInsideMosque(nextState);
    const now = new Date().toLocaleTimeString('ar-SA', { hour: '2-digit', minute: '2-digit' });
    if (nextState) {
      setSilenceLog(prev => [{ time: now, event: `تم تفعيل الوضع الصامت بالمسجد (${selectedMosqueName})` }, ...prev]);
    } else {
      setSilenceLog(prev => [{ time: now, event: `تم الخروج من نطاق المسجد واستعادة الصوت` }, ...prev]);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-blue-950/80 via-emerald-900/50 to-slate-900 border border-blue-500/30 rounded-3xl p-6 md:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-blue-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold mb-3 border border-blue-500/30">
              <Clock className="w-3.5 h-3.5" />
              الإنتاجية وتنظيم الحياة الإيمانية
            </div>
            <h2 className="text-2xl md:text-3xl font-extrabold text-white">إنتاجية المسلم ذكية بالكامل</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              إدارة المهام المجدولة حول فترات الصلوات، محاكاة الوضع الهادئ التلقائي بالمسجد، وأذكار ذكية معتمدة على سياق المستشعرات.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/80 p-1.5 rounded-2xl border border-slate-800">
            <button
              onClick={() => setActiveTab('planner')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'planner' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              منظم الصلاة
            </button>
            <button
              onClick={() => setActiveTab('geofence')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'geofence' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الوضع الصامت
            </button>
            <button
              onClick={() => setActiveTab('contextAthkar')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
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
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <CheckSquare className="w-5 h-5 text-blue-400" />
                منظم اليوم المعتمد على مواقيت الصلاة (Salah-Centric Planner)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                بدلاً من الساعات الثابتة (9:00 ص، 2:00 م)، نظّم حياتك حول الفترات الإيمانية التي تتناغم مع حركتك الحيوية.
              </p>
            </div>
            <div className="flex flex-wrap items-center gap-2 bg-slate-950 px-3.5 py-2 rounded-2xl border border-slate-800 text-xs text-emerald-400 font-bold">
              <span>الفجر: 04:45 ص</span> • <span>الظهر: 12:15 م</span> • <span>العصر: 03:30 م</span> • <span>المغرب: 06:10 م</span> • <span>العشاء: 07:40 م</span>
            </div>
          </div>

          {/* Add Task Control */}
          <div className="flex flex-col md:flex-row gap-3">
            <input
              type="text"
              value={newTaskText}
              onChange={(e) => setNewTaskText(e.target.value)}
              placeholder="اكتب مهمة جديدة ترغب بربطها بفترة الصلاة..."
              className="flex-1 bg-slate-950 border border-slate-700 rounded-2xl px-4 py-3 text-xs md:text-sm text-slate-100 focus:outline-none focus:border-blue-500"
            />
            <select
              value={selectedPeriod}
              onChange={(e) => setSelectedPeriod(e.target.value)}
              className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-2xl px-4 py-3 focus:outline-none focus:border-blue-500 font-semibold"
            >
              {periods.map((p, i) => (
                <option key={i} value={p}>{p}</option>
              ))}
            </select>
            <button
              onClick={addTask}
              className="px-6 py-3 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-2xl shadow-lg transition-all flex items-center justify-center gap-2 shrink-0"
            >
              <Plus className="w-4 h-4" />
              إضافة المهمة
            </button>
          </div>

          {/* Task Groups */}
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {periods.map((period, pIdx) => {
              const periodTasks = tasks.filter(t => t.period === period);
              return (
                <div key={pIdx} className="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-3 flex flex-col justify-between">
                  <div>
                    <h4 className="text-xs font-bold text-blue-300 border-b border-slate-800 pb-2.5 flex items-center justify-between">
                      <span>{period}</span>
                      <span className="text-slate-500 text-[11px] font-normal">({periodTasks.length} مهام)</span>
                    </h4>
                    <div className="space-y-2 mt-3">
                      {periodTasks.length === 0 ? (
                        <p className="text-[11px] text-slate-600 italic py-2 text-center">لا توجد مهام في هذه الفترة</p>
                      ) : (
                        periodTasks.map(task => (
                          <div
                            key={task.id}
                            className={`flex items-center justify-between p-2.5 rounded-xl border transition-all ${
                              task.completed
                                ? 'bg-slate-900/40 border-slate-900 text-slate-500 line-through'
                                : 'bg-slate-900 border-slate-800 text-slate-200 hover:border-slate-700'
                            }`}
                          >
                            <div
                              onClick={() => toggleTask(task.id)}
                              className="flex items-center gap-2 cursor-pointer flex-1"
                            >
                              <CheckCircle2 className={`w-4 h-4 shrink-0 ${task.completed ? 'text-emerald-500' : 'text-slate-600'}`} />
                              <span className="text-xs font-medium leading-tight">{task.text}</span>
                            </div>
                            <button
                              onClick={() => deleteTask(task.id)}
                              className="text-slate-500 hover:text-red-400 p-1 shrink-0 transition-colors"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        ))
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* TAB 2: GEOFENCING SILENT MODE */}
      {activeTab === 'geofence' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <MapPin className="w-5 h-5 text-blue-400" />
                الوضع الهادئ التلقائي في المسجد (Geofencing Simulator)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                التعرف الجغرافي على محيط المسجد وكتم رنين الهاتف تلقائياً أثناء الصلاة وإعادته بعد الانتهاء.
              </p>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400">المسجد المحدد:</span>
              <select
                value={selectedMosqueName}
                onChange={(e) => setSelectedMosqueName(e.target.value)}
                className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-xl px-3 py-1.5 focus:outline-none focus:border-blue-500"
              >
                <option value="جامع الراجحي الكبير">جامع الراجحي الكبير</option>
                <option value="مسجد قباء">مسجد قباء</option>
                <option value="مسجد الحي المحلي">مسجد الحي المحلي</option>
              </select>
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-6 items-start">
            {/* Simulation Device Card */}
            <div className="bg-slate-950 p-6 rounded-3xl border border-slate-800 text-center space-y-4 shadow-lg">
              <div className="inline-flex p-5 rounded-full bg-slate-900 border border-slate-800">
                {isInsideMosque ? (
                  <VolumeX className="w-14 h-14 text-emerald-400 animate-pulse" />
                ) : (
                  <Volume2 className="w-14 h-14 text-slate-500" />
                )}
              </div>
              <div>
                <h4 className="text-lg font-bold text-white">حالة صوت الهاتف</h4>
                <p className={`text-xs mt-1 font-bold ${isInsideMosque ? 'text-emerald-400' : 'text-slate-400'}`}>
                  {isInsideMosque ? `الوضع الصامت مفعّل (داخل نطاق ${selectedMosqueName})` : 'الوضع العام (رنين طبيعي خارج المسجد)'}
                </p>
              </div>

              <button
                onClick={handleToggleMosqueGeofence}
                className={`w-full py-3.5 px-4 rounded-2xl text-xs font-bold transition-all shadow-lg ${
                  isInsideMosque
                    ? 'bg-amber-600 hover:bg-amber-500 text-white shadow-amber-950'
                    : 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-emerald-950'
                }`}
              >
                {isInsideMosque ? 'محاكاة الخروج من المسجد (إعادة الرنين)' : 'محاكاة دخول حرم المسجد الآن (تفعيل الصامت)'}
              </button>

              <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800 text-right text-[11px] text-slate-400 space-y-1">
                <span className="font-bold text-blue-300 block">سجل أحداث النطاق الصامت:</span>
                {silenceLog.slice(0, 3).map((log, idx) => (
                  <div key={idx} className="flex justify-between text-[10px] text-slate-300 border-b border-slate-800/60 pb-1">
                    <span>{log.event}</span>
                    <span className="text-slate-500 font-mono">{log.time}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Geofence Parameters & OS Limitations */}
            <div className="space-y-4 bg-slate-950/80 p-6 rounded-3xl border border-slate-800 text-xs">
              <h5 className="font-bold text-blue-300 flex items-center gap-1.5 border-b border-slate-800 pb-2">
                ⚙️ المعايير الهندسية للنطاق الصامت:
              </h5>

              <div className="space-y-2">
                <div className="flex items-center justify-between p-3 bg-slate-900 rounded-xl">
                  <span className="text-slate-300">نصف قطر الدائرة الجغرافية:</span>
                  <span className="text-blue-400 font-bold">50 متر حول المسجد</span>
                </div>
                <div className="flex items-center justify-between p-3 bg-slate-900 rounded-xl">
                  <span className="text-slate-300">المدة الزمنية التلقائية للصامت:</span>
                  <select
                    value={autoRestoreMinutes}
                    onChange={(e) => setAutoRestoreMinutes(Number(e.target.value))}
                    className="bg-slate-950 text-emerald-400 font-bold text-xs p-1 rounded border border-slate-700"
                  >
                    <option value={15}>15 دقيقة</option>
                    <option value={20}>20 دقيقة</option>
                    <option value={30}>30 دقيقة</option>
                  </select>
                </div>
              </div>

              <div className="p-3.5 bg-amber-950/20 border border-amber-500/30 rounded-2xl space-y-1.5 text-amber-200 text-[11px] leading-relaxed">
                <span className="font-bold flex items-center gap-1 text-amber-300">
                  <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                  تحليل الصعوبة التقنية والبديل الآمن:
                </span>
                <p>
                  نظراً لأن أنظمة iOS لا تسمح بالتحكم بمفتاح الصوت البرمجي بدون تدخل المستخدم، يوفر التطبيق بديلاً عبر <strong>إشعار تفاعلي فوري بلمسة واحدة (Interactive Focus Mode Notification)</strong> عند الاقتراب من المسجد.
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: CONTEXT ATHKAR */}
      {activeTab === 'contextAthkar' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Bell className="w-5 h-5 text-blue-400" />
                أذكار ذكية مرتبطة بالسياق وحساسات الهاتف
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                عرض الذكر المناسب للحظتك الفعلية بدلاً من الإشعارات العشوائية المشتتة.
              </p>
            </div>
            <span className="text-xs bg-blue-500/10 text-blue-300 px-3 py-1 rounded-full border border-blue-500/20 font-bold">
              محاكي مستشعرات الجهاز
            </span>
          </div>

          <div className="grid md:grid-cols-4 gap-3">
            <button
              onClick={() => setSimulatedSensor('bluetooth')}
              className={`p-4 rounded-2xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'bluetooth'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg shadow-blue-950'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Bluetooth className="w-5 h-5 text-blue-400" />
                <span className="text-[10px] bg-blue-500/20 text-blue-300 px-2 py-0.5 rounded font-bold">بلوتوث</span>
              </div>
              <h4 className="font-bold text-xs text-slate-100">ركوب السيارة</h4>
              <p className="text-[11px] text-slate-400 leading-snug">الاقتران بنظام البلوتوث الخاص بالسيارة.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('speed')}
              className={`p-4 rounded-2xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'speed'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg shadow-blue-950'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Navigation className="w-5 h-5 text-amber-400" />
                <span className="text-[10px] bg-amber-500/20 text-amber-300 px-2 py-0.5 rounded font-bold">GPS سرعة</span>
              </div>
              <h4 className="font-bold text-xs text-slate-100">السفر السريع</h4>
              <p className="text-[11px] text-slate-400 leading-snug">التحرك بسرعة تتجاوز 90 كم/س خارج النطاق العمراني.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('mall')}
              className={`p-4 rounded-2xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'mall'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg shadow-blue-950'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <MapPin className="w-5 h-5 text-emerald-400" />
                <span className="text-[10px] bg-emerald-500/20 text-emerald-300 px-2 py-0.5 rounded font-bold">موقع جغرافي</span>
              </div>
              <h4 className="font-bold text-xs text-slate-100">دخول السوق</h4>
              <p className="text-[11px] text-slate-400 leading-snug">التواجد بالقرب من المراكز التجارية والأسواق.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('home')}
              className={`p-4 rounded-2xl border text-right transition-all space-y-2 ${
                simulatedSensor === 'home'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg shadow-blue-950'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Compass className="w-5 h-5 text-purple-400" />
                <span className="text-[10px] bg-purple-500/20 text-purple-300 px-2 py-0.5 rounded font-bold">Wi-Fi المنزل</span>
              </div>
              <h4 className="font-bold text-xs text-slate-100">دخول المنزل</h4>
              <p className="text-[11px] text-slate-400 leading-snug">الاتصال بشبكة واي فاي المنزل بعد العودة.</p>
            </button>
          </div>

          {/* Sensor Output Alert Card */}
          <div className="p-6 bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 border border-blue-500/40 rounded-3xl space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <span className="text-xs font-bold text-blue-400 flex items-center gap-2">
                <Bell className="w-4 h-4 text-blue-400" />
                الإشعار الذكي للحدث اللحظي المكتشف:
              </span>
              <span className="text-[10px] text-slate-400 bg-slate-800 px-2.5 py-1 rounded-lg font-mono">
                مستشعر نشط
              </span>
            </div>

            {simulatedSensor === 'bluetooth' && (
              <div className="space-y-2">
                <h5 className="text-xl font-serif font-bold text-emerald-300 leading-relaxed text-center py-2">
                  "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ"
                </h5>
                <div className="flex items-center justify-between text-xs text-slate-400 bg-slate-950 p-3 rounded-xl border border-slate-800">
                  <span>دعاء الركوب • تم تفعيله عند الاتصال ببلوتوث السيارة.</span>
                  <span className="text-emerald-400 font-bold">صحيح مسلم</span>
                </div>
              </div>
            )}

            {simulatedSensor === 'speed' && (
              <div className="space-y-2">
                <h5 className="text-xl font-serif font-bold text-amber-300 leading-relaxed text-center py-2">
                  "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، وَمِنَ الْعَمَلِ مَا تَرْضَى"
                </h5>
                <div className="flex items-center justify-between text-xs text-slate-400 bg-slate-950 p-3 rounded-xl border border-slate-800">
                  <span>دعاء السفر • تم تفعيله للتحرك السريع على الطرق السريعة.</span>
                  <span className="text-amber-400 font-bold">سنن أبي داود</span>
                </div>
              </div>
            )}

            {simulatedSensor === 'mall' && (
              <div className="space-y-2">
                <h5 className="text-lg font-serif font-bold text-teal-300 leading-relaxed text-center py-2">
                  "لا إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لا يَمُوتُ، بِيَدِهِ الْخَيْرُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ"
                </h5>
                <div className="flex items-center justify-between text-xs text-slate-400 bg-slate-950 p-3 rounded-xl border border-slate-800">
                  <span>دعاء دخول السوق (ألف ألف حسنة) • تم تفعيله بالقرب من المول التجاري.</span>
                  <span className="text-teal-400 font-bold">سنن الترمذي</span>
                </div>
              </div>
            )}

            {simulatedSensor === 'home' && (
              <div className="space-y-2">
                <h5 className="text-lg font-serif font-bold text-purple-300 leading-relaxed text-center py-2">
                  "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا - السلام عليكم"
                </h5>
                <div className="flex items-center justify-between text-xs text-slate-400 bg-slate-950 p-3 rounded-xl border border-slate-800">
                  <span>دعاء دخول المنزل وإفشاء السلام • تم تفعيله عند الاقتران بشبكة واي فاي المنزل.</span>
                  <span className="text-purple-400 font-bold">سنن أبي داود</span>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
