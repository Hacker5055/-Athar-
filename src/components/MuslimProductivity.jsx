import React, { useState } from 'react';
import { Clock, CheckSquare, Bell, MapPin, VolumeX, Bluetooth, Smartphone, Plus, Trash2, CheckCircle2, ShieldCheck, AlertCircle } from 'lucide-react';

export default function MuslimProductivity() {
  const [activeTab, setActiveTab] = useState('planner'); // 'planner', 'geofence', 'contextAthkar'

  // Salah Centric Planner Tasks
  const [tasks, setTasks] = useState([
    { id: 1, text: 'أذكار الصباح وتلاوة حزب من سورة البقرة', period: 'ما بين الفجر والشروق', completed: true },
    { id: 2, text: 'جلسة العمل العميق والمهام الأكثر تركيزاً', period: 'ما بين الشروق والظهر', completed: false },
    { id: 3, text: 'سنة الظهر البعدية وقيلولة استعادة النشاط (20 دقيقة)', period: 'ما بين الظهر والعصر', completed: false },
    { id: 4, text: 'قراءة أذكار المساء والمشي في الهواء الطلق', period: 'ما بين العصر والمغرب', completed: false },
    { id: 5, text: 'جلسة عائلية ومطالعة نافعة', period: 'ما بين المغرب والعشاء', completed: false },
    { id: 6, text: 'صلاة الوتر وتسميع ورد الحفظ اليومي قبل النوم', period: 'ما بعد العشاء', completed: false },
  ]);

  const [newTaskText, setNewTaskText] = useState('');
  const [selectedPeriod, setSelectedPeriod] = useState('ما بين الفجر والشروق');

  // Geofencing Simulator State
  const [isInsideMosque, setIsInsideMosque] = useState(false);
  const [autoMuteEnabled, setAutoMuteEnabled] = useState(true);

  // Smart Context Athkar State
  const [simulatedSensor, setSimulatedSensor] = useState('bluetooth'); // 'bluetooth', 'speed', 'mall'

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
              الإنتاجية وتنظيم الحياة المعتمدة على الصلاة
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">إنتاجية المسلم الذكية</h2>
            <p className="text-slate-300 text-sm mt-1">
              منظم اليوم المرتكز حول مواقيت الصلاة، محاكاة النطاق الجغرافي الصامت في المسجد، والأذكار الذكية حسب الحركة والمستشعرات.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('planner')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'planner' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              منظم الصلاة
            </button>
            <button
              onClick={() => setActiveTab('geofence')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'geofence' ? 'bg-blue-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الوضع الصامت
            </button>
            <button
              onClick={() => setActiveTab('contextAthkar')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
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
                توزيع المهام الحياتية والدينية بين أوقات الصلوات ليتناغم يوم المسلم مع نداء الحق بدلاً من الساعات الجافة.
              </p>
            </div>
            <div className="flex flex-wrap items-center gap-2 bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800 text-xs text-emerald-400 font-mono">
              <span>الفجر: 04:45 ص</span> | <span>الظهر: 12:15 م</span> | <span>العصر: 03:30 م</span> | <span>المغرب: 06:10 م</span> | <span>العشاء: 07:40 م</span>
            </div>
          </div>

          {/* Add Task Control */}
          <div className="flex flex-col md:flex-row gap-3">
            <input
              type="text"
              value={newTaskText}
              onChange={(e) => setNewTaskText(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && addTask()}
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
              className="px-5 py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-xl shadow-md transition-all flex items-center justify-center gap-1.5 cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              إضافة مهمة
            </button>
          </div>

          {/* Task Groups */}
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {periods.map((period, pIdx) => {
              const periodTasks = tasks.filter(t => t.period === period);

              return (
                <div key={pIdx} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-3 flex flex-col justify-between">
                  <div>
                    <h4 className="text-xs font-bold text-blue-300 border-b border-slate-800/80 pb-2 flex items-center justify-between">
                      <span>{period}</span>
                      <span className="text-slate-500 font-normal">({periodTasks.length} مهام)</span>
                    </h4>

                    <div className="space-y-2 mt-3">
                      {periodTasks.length === 0 ? (
                        <p className="text-[11px] text-slate-600 text-center py-4">لا توجد مهام مسجلة لهذه الفترة</p>
                      ) : (
                        periodTasks.map(task => (
                          <div
                            key={task.id}
                            className={`flex items-center justify-between p-2.5 rounded-lg border transition-all ${
                              task.completed
                                ? 'bg-slate-900/40 border-slate-800/60 text-slate-500 line-through'
                                : 'bg-slate-900 border-slate-700 text-slate-200 hover:border-slate-600'
                            }`}
                          >
                            <div
                              onClick={() => toggleTask(task.id)}
                              className="flex items-center gap-2 cursor-pointer flex-1"
                            >
                              <CheckCircle2 className={`w-4 h-4 shrink-0 ${task.completed ? 'text-emerald-500' : 'text-slate-600'}`} />
                              <span className="text-xs font-medium">{task.text}</span>
                            </div>
                            <button
                              onClick={() => deleteTask(task.id)}
                              className="text-slate-500 hover:text-red-400 p-1 cursor-pointer"
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
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-4">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <MapPin className="w-5 h-5 text-blue-400" />
              الوضع الصامت التلقائي في المسجد (Geofencing Simulator)
            </h3>
            <p className="text-slate-400 text-xs">
              محاكاة للتعرف التلقائي على دخول نطاق المسجد الجغرافي أو أوقات إقامة الصلاة لكتم صوت الهاتف وحفظ خشوع المصلين.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6 items-stretch">
            {/* Status Simulator Card */}
            <div className="bg-slate-950 p-6 rounded-2xl border border-slate-800 text-center space-y-5 flex flex-col justify-between">
              <div>
                <div className="inline-flex p-5 rounded-full bg-slate-900 border border-slate-800 mb-3 shadow-inner">
                  <VolumeX className={`w-14 h-14 transition-all duration-300 ${isInsideMosque ? 'text-emerald-400 scale-110' : 'text-slate-600'}`} />
                </div>
                <div>
                  <h4 className="text-lg font-bold text-white">حالة الهاتف الحالية</h4>
                  <div className="mt-2 inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-bold border">
                    {isInsideMosque ? (
                      <span className="text-emerald-400 bg-emerald-950/60 border-emerald-500/40 px-3 py-1 rounded-full flex items-center gap-1.5">
                        <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
                        الوضع الصامت مُفعّل (داخل نطاق جامع الراجحي)
                      </span>
                    ) : (
                      <span className="text-slate-400 bg-slate-900 border-slate-700 px-3 py-1 rounded-full">
                        الوضع العادي (خارج نطاق المساجد)
                      </span>
                    )}
                  </div>
                </div>
              </div>

              <div className="space-y-3">
                <button
                  onClick={() => setIsInsideMosque(!isInsideMosque)}
                  className={`w-full py-3.5 px-4 rounded-xl text-xs font-bold transition-all shadow-md cursor-pointer ${
                    isInsideMosque
                      ? 'bg-amber-600 hover:bg-amber-500 text-white'
                      : 'bg-emerald-600 hover:bg-emerald-500 text-white'
                  }`}
                >
                  {isInsideMosque ? 'محاكاة الخروج من المسجد (إعادة الصوت)' : 'محاكاة دخول نطاق المسجد الآن (تفعيل الصامت)'}
                </button>
              </div>
            </div>

            {/* Technical Feasibility Notes */}
            <div className="space-y-4 bg-slate-950/70 p-6 rounded-2xl border border-slate-800 text-xs flex flex-col justify-between">
              <div className="space-y-3">
                <h5 className="font-bold text-blue-300 flex items-center gap-2 border-b border-slate-800 pb-2">
                  <ShieldCheck className="w-4 h-4 text-emerald-400" />
                  إعدادات النطاق والتكامل الذكي:
                </h5>
                <div className="flex items-center justify-between p-2.5 bg-slate-900 rounded-lg">
                  <span className="text-slate-300">نطاق التفعيل الجغرافي:</span>
                  <span className="text-blue-400 font-mono font-bold">50 متراً دائرياً</span>
                </div>
                <div className="flex items-center justify-between p-2.5 bg-slate-900 rounded-lg">
                  <span className="text-slate-300">إعادة الصوت الطبيعي:</span>
                  <span className="text-emerald-400 font-mono font-bold">بعد 25 دقيقة من الصلاة</span>
                </div>
                <div className="flex items-center justify-between p-2.5 bg-slate-900 rounded-lg">
                  <span className="text-slate-300">التكامل مع أنظمة iOS Focus و Android:</span>
                  <span className="text-emerald-400 font-bold">مدعوم عبر Siri Shortcuts</span>
                </div>
              </div>

              <div className="bg-amber-950/20 p-3 rounded-xl border border-amber-500/30 text-[11px] text-amber-200/90 leading-relaxed">
                <AlertCircle className="w-4 h-4 text-amber-400 inline mr-1 ml-1" />
                <span className="font-bold">تنبيه تقني:</span> تحظر أبل كتم صوت الهاتف بالكامل في الخلفية دون موافقة المستخدم، لذا صمم تطبيق "أثر" البديل الهندسي الأذكى عبر تفعيل إشعار تفاعلي فوري بلمسة واحدة أو عبر نمط التركيز (Focus Mode Automation).
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: CONTEXT ATHKAR */}
      {activeTab === 'contextAthkar' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-4">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <Bell className="w-5 h-5 text-blue-400" />
              أذكار ذكية مرتبطة بالسياق والمستشعرات
            </h3>
            <p className="text-slate-400 text-xs">
              بدلاً من الإشعارات العشوائية المشتتة، تظهر الأذكار في لحظتها المناسبة استناداً لحالة الجهاز وحركتك الفعلية.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-3">
            <button
              onClick={() => setSimulatedSensor('bluetooth')}
              className={`p-4 rounded-xl border text-right transition-all space-y-2 cursor-pointer ${
                simulatedSensor === 'bluetooth'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Bluetooth className="w-5 h-5 text-blue-400" />
                <span className="text-[10px] bg-blue-500/20 text-blue-300 px-2 py-0.5 rounded font-mono">بلوتوث السيارة</span>
              </div>
              <h4 className="font-bold text-xs text-slate-200">الاقتران بسيارتك</h4>
              <p className="text-[11px] text-slate-400">إظهار دعاء ركوب الدابة فور الاتصال بنظام السيارة.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('speed')}
              className={`p-4 rounded-xl border text-right transition-all space-y-2 cursor-pointer ${
                simulatedSensor === 'speed'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <Smartphone className="w-5 h-5 text-amber-400" />
                <span className="text-[10px] bg-amber-500/20 text-amber-300 px-2 py-0.5 rounded font-mono">مستشعر السرعة</span>
              </div>
              <h4 className="font-bold text-xs text-slate-200">التحرك بسرعة طريق السفر</h4>
              <p className="text-[11px] text-slate-400">تجاوز سرعة 90 كم/س لمسافة طويلة يستدعي دعاء السفر.</p>
            </button>

            <button
              onClick={() => setSimulatedSensor('mall')}
              className={`p-4 rounded-xl border text-right transition-all space-y-2 cursor-pointer ${
                simulatedSensor === 'mall'
                  ? 'bg-blue-950/80 border-blue-500 text-white shadow-lg'
                  : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="flex justify-between items-center">
                <MapPin className="w-5 h-5 text-emerald-400" />
                <span className="text-[10px] bg-emerald-500/20 text-emerald-300 px-2 py-0.5 rounded font-mono">الموقع الجغرافي</span>
              </div>
              <h4 className="font-bold text-xs text-slate-200">التواجد في الأسواق والمولات</h4>
              <p className="text-[11px] text-slate-400">تذكير بدعاء دخول السوق ونيل مليون حسنة.</p>
            </button>
          </div>

          {/* Sensor Notification Display Card */}
          {simulatedSensor !== 'none' && (
            <div className="p-6 bg-gradient-to-r from-slate-950 via-slate-900 to-slate-950 border border-blue-500/40 rounded-2xl space-y-3 animate-fade-in shadow-2xl">
              <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                <span className="text-xs font-bold text-blue-400 flex items-center gap-2">
                  <Bell className="w-4 h-4 animate-bounce" />
                  بطاقة الذكر الذكية الحالية:
                </span>
                <span className="text-[11px] text-emerald-400 bg-emerald-950/60 border border-emerald-500/30 px-2.5 py-0.5 rounded-full font-mono">
                  تم الاستشعار بنجاح
                </span>
              </div>

              {simulatedSensor === 'bluetooth' && (
                <div className="space-y-2">
                  <span className="text-xs text-slate-400 block">دعاء ركوب الدابة والسيارة:</span>
                  <h5 className="text-lg md:text-xl font-serif font-bold text-emerald-300 leading-relaxed text-center py-2">
                    "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ"
                  </h5>
                  <p className="text-xs text-slate-400 text-center">أخرجه أبو داود والترمذي • تذكير ذكي عند ركوب المركبة</p>
                </div>
              )}

              {simulatedSensor === 'speed' && (
                <div className="space-y-2">
                  <span className="text-xs text-slate-400 block">دعاء السفر:</span>
                  <h5 className="text-lg md:text-xl font-serif font-bold text-amber-300 leading-relaxed text-center py-2">
                    "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، وَمِنَ الْعَمَلِ مَا تَرْضَى، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا وَاطْوِ عَنَّا بُعْدَهُ"
                  </h5>
                  <p className="text-xs text-slate-400 text-center">صحيح مسلم • تم الاستشعار بناءً على سرعة الطريق السريع</p>
                </div>
              )}

              {simulatedSensor === 'mall' && (
                <div className="space-y-2">
                  <span className="text-xs text-slate-400 block">دعاء دخول السوق:</span>
                  <h5 className="text-lg md:text-xl font-serif font-bold text-teal-300 leading-relaxed text-center py-2">
                    "لا إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الحَمْدُ يُحْيِي وَيُمِيتُ وَهُوَ حَيٌّ لا يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ"
                  </h5>
                  <p className="text-xs text-slate-400 text-center">سنن الترمذي • كُتب له ألف ألف حسنة ومُحي عنه ألف ألف سيئة</p>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
