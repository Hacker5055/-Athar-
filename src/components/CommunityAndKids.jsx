import React, { useState } from 'react';
import { Users, MapPin, Gamepad2, Star, CheckCircle, Plus, ShieldCheck, Heart, Sparkles, ChevronLeft, Award, Lock, BookOpen } from 'lucide-react';

export default function CommunityAndKids() {
  const [activeTab, setActiveTab] = useState('mosqueMap'); // 'mosqueMap', 'groupKhatma', 'interactiveKids'

  // Mosque Map Crowdsourced Data
  const [mosques, setMosques] = useState([
    {
      id: 1,
      name: 'جامع الشيخ سليمان الراجحي',
      distance: '1.2 كم',
      rating: 4.9,
      womenSection: true,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 184
    },
    {
      id: 2,
      name: 'مسجد التوحيد',
      distance: '0.5 كم',
      rating: 4.3,
      womenSection: false,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 52
    },
    {
      id: 3,
      name: 'جامع الملك فهد الكبير',
      distance: '2.8 كم',
      rating: 4.8,
      womenSection: true,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 210
    }
  ]);

  const [showAddMosqueModal, setShowAddMosqueModal] = useState(false);
  const [newMosqueName, setNewMosqueName] = useState('');

  // Khatma Group State
  const [khatmaProgress, setKhatmaProgress] = useState(68);
  const [userContribution, setUserContribution] = useState(2); // Parts read
  const [isAnonymous, setIsAnonymous] = useState(true);

  // Group members list
  const groupMembers = [
    { name: 'عضو #1 (مجهول)', parts: 5, color: 'text-emerald-400' },
    { name: 'عضو #2 (مجهول)', parts: 4, color: 'text-blue-400' },
    { name: isAnonymous ? 'أنت (عضو #3 - مجهول)' : 'أحمد العتيبي (أنت)', parts: userContribution, color: 'text-purple-400' },
    { name: 'عضو #4 (مجهول)', parts: 6, color: 'text-amber-400' },
    { name: 'عضو #5 (مجهول)', parts: 3.5, color: 'text-teal-400' },
  ];

  // Interactive Story State
  const [currentScenario, setCurrentScenario] = useState('honesty'); // 'honesty', 'patience'
  const [storyStep, setStoryStep] = useState('start'); // 'start', 'choiceA', 'choiceB'

  const handleAddNewMosque = () => {
    if (!newMosqueName.trim()) return;
    setMosques([
      ...mosques,
      {
        id: Date.now(),
        name: newMosqueName,
        distance: '0.8 كم',
        rating: 5.0,
        womenSection: true,
        cleanWashrooms: true,
        wheelchairAccess: true,
        elderlyChairs: true,
        reviewsCount: 1
      }
    ]);
    setNewMosqueName('');
    setShowAddMosqueModal(false);
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-purple-950/80 via-emerald-800/50 to-slate-900 border border-purple-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-purple-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-purple-500/20 text-purple-300 text-xs font-semibold mb-3 border border-purple-500/30">
              <Users className="w-3.5 h-3.5" />
              المجتمع التشاركي والجيل الجديد
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المجتمع والجيل الجديد</h2>
            <p className="text-slate-300 text-sm mt-1">
              خريطة مرافق المساجد التشاركية، تحديات الختمات العائلية بدون رياء، والقصص الأخلاقية التفاعلية للأطفال والناشئة.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('mosqueMap')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'mosqueMap' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              خريطة المساجد
            </button>
            <button
              onClick={() => setActiveTab('groupKhatma')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'groupKhatma' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الختمات التنافسية
            </button>
            <button
              onClick={() => setActiveTab('interactiveKids')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'interactiveKids' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              قصص الأطفال
            </button>
          </div>
        </div>
      </div>

      {/* TAB 1: CROWDSOURCED MOSQUE MAP */}
      {activeTab === 'mosqueMap' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <MapPin className="w-5 h-5 text-purple-400" />
                خريطة مرافق المساجد التفاعلية (Crowdsourced Mosque Map)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                تحديث وتقييم خدمات المساجد من المصلين أنفسهم لخدمة العائلات، كبار السن، وذوي الهمم.
              </p>
            </div>
            <button
              onClick={() => setShowAddMosqueModal(true)}
              className="px-4 py-2 bg-purple-600 hover:bg-purple-500 text-white text-xs font-semibold rounded-xl transition-all flex items-center gap-1.5 shadow-md cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              إضافة أو تحديث خدمات مسجد
            </button>
          </div>

          {/* Add Mosque Modal */}
          {showAddMosqueModal && (
            <div className="bg-slate-950 p-5 rounded-2xl border border-purple-500/40 space-y-3 animate-fade-in shadow-xl">
              <h4 className="text-sm font-bold text-white">تحديث بيانات مسجد جديد:</h4>
              <div className="flex gap-3">
                <input
                  type="text"
                  value={newMosqueName}
                  onChange={(e) => setNewMosqueName(e.target.value)}
                  placeholder="اسم المسجد والحي..."
                  className="flex-1 bg-slate-900 border border-slate-700 text-xs rounded-xl p-2.5 text-white focus:outline-none focus:border-purple-500"
                />
                <button
                  onClick={handleAddNewMosque}
                  className="px-4 py-2 bg-purple-600 text-white text-xs font-bold rounded-xl cursor-pointer"
                >
                  حفظ البيانات
                </button>
                <button
                  onClick={() => setShowAddMosqueModal(false)}
                  className="px-3 py-2 bg-slate-800 text-slate-300 text-xs rounded-xl cursor-pointer"
                >
                  إلغاء
                </button>
              </div>
            </div>
          )}

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {mosques.map((mosque) => (
              <div key={mosque.id} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-4 hover:border-purple-500/40 transition-all flex flex-col justify-between shadow-md">
                <div className="space-y-2">
                  <div className="flex justify-between items-start">
                    <div>
                      <h4 className="font-bold text-white text-sm">{mosque.name}</h4>
                      <span className="text-[11px] text-slate-400">يبعد {mosque.distance} عن موقعك</span>
                    </div>
                    <div className="flex items-center gap-1 bg-amber-500/20 text-amber-300 px-2 py-0.5 rounded-full text-xs font-bold border border-amber-500/30">
                      <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                      {mosque.rating} <span className="text-[10px] text-slate-400 font-normal">({mosque.reviewsCount})</span>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-2 text-[11px] pt-2">
                    <div className={`p-2 rounded-lg flex items-center gap-1.5 ${mosque.womenSection ? 'bg-emerald-950/50 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900 text-slate-500'}`}>
                      <CheckCircle className="w-3.5 h-3.5 shrink-0" />
                      مصلى سيدات
                    </div>
                    <div className={`p-2 rounded-lg flex items-center gap-1.5 ${mosque.cleanWashrooms ? 'bg-emerald-950/50 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900 text-slate-500'}`}>
                      <CheckCircle className="w-3.5 h-3.5 shrink-0" />
                      دورات مياه نظيفة
                    </div>
                    <div className={`p-2 rounded-lg flex items-center gap-1.5 ${mosque.wheelchairAccess ? 'bg-emerald-950/50 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900 text-slate-500'}`}>
                      <CheckCircle className="w-3.5 h-3.5 shrink-0" />
                      مدخل ذوي الهمم
                    </div>
                    <div className={`p-2 rounded-lg flex items-center gap-1.5 ${mosque.elderlyChairs ? 'bg-emerald-950/50 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900 text-slate-500'}`}>
                      <CheckCircle className="w-3.5 h-3.5 shrink-0" />
                      كراسي كبار السن
                    </div>
                  </div>
                </div>

                <div className="pt-3 border-t border-slate-900 flex justify-between items-center text-[11px]">
                  <span className="text-slate-500">تم التحقق من المصلين</span>
                  <button className="text-purple-400 hover:text-purple-300 font-semibold cursor-pointer">
                    إضافة تقييم مرافق
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 2: GROUP KHATMA */}
      {activeTab === 'groupKhatma' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Users className="w-5 h-5 text-purple-400" />
                ختمات وتحديات العائلة والأصدقاء (Private Groups)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                تنافس تحفيزي للورد القرآني والسنن مع ميزة الإخفاء التام للهوية (دون رياء أو حرج).
              </p>
            </div>
            <div className="flex items-center gap-3">
              <button
                onClick={() => setIsAnonymous(!isAnonymous)}
                className={`text-xs px-3 py-1.5 rounded-full border transition-all cursor-pointer flex items-center gap-1.5 ${
                  isAnonymous ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' : 'bg-slate-800 text-slate-300 border-slate-700'
                }`}
              >
                <ShieldCheck className="w-3.5 h-3.5" />
                {isAnonymous ? 'وضع إخفاء الهوية مفعّل' : 'الاسم ظاهر'}
              </button>
              <span className="text-xs bg-purple-500/20 text-purple-300 px-3 py-1.5 rounded-full border border-purple-500/30 font-semibold">
                غرفة: ختمة رمضان المبارك 🌙
              </span>
            </div>
          </div>

          <div className="bg-slate-950 p-6 rounded-2xl border border-slate-800 space-y-6">
            <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-300 font-bold">
                <span>نسبة إنجاز الختمة الجماعية:</span>
                <span className="text-purple-400 font-mono text-sm">{khatmaProgress}% (20.5 جزء مكتمل من 30)</span>
              </div>
              <div className="w-full bg-slate-900 h-3 rounded-full overflow-hidden border border-slate-800">
                <div className="bg-gradient-to-r from-purple-600 to-emerald-500 h-full transition-all duration-500" style={{ width: `${khatmaProgress}%` }}></div>
              </div>
            </div>

            <div className="grid md:grid-cols-3 gap-4 text-xs">
              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400">مساهمتك المسجلة:</span>
                <p className="text-lg font-bold text-emerald-400 font-mono">{userContribution} أجزاء مقروءة</p>
              </div>

              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400">حالة ظهور الاسم للمجموعة:</span>
                <p className="text-xs font-bold text-purple-300 flex items-center gap-1 mt-1">
                  <Lock className="w-3.5 h-3.5 text-purple-400" />
                  {isAnonymous ? 'مجهول (عضو #3) للحفاظ على الإخلاص' : 'الاسم معلن للمشاركين'}
                </p>
              </div>

              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 flex flex-col justify-center">
                <button
                  onClick={() => {
                    setUserContribution(userContribution + 1);
                    setKhatmaProgress(Math.min(100, khatmaProgress + 3.3));
                  }}
                  className="w-full py-2.5 bg-purple-600 hover:bg-purple-500 text-white font-bold rounded-lg transition-all cursor-pointer shadow-md text-xs"
                >
                  + تسجيل قراءة جزء جديد الآن
                </button>
              </div>
            </div>

            {/* Members Progress */}
            <div className="space-y-2 pt-2 border-t border-slate-900">
              <span className="text-xs font-bold text-slate-300 block">سجل إنجاز أعضاء الغرفة:</span>
              <div className="grid grid-cols-2 md:grid-cols-5 gap-2 text-xs">
                {groupMembers.map((member, mIdx) => (
                  <div key={mIdx} className="bg-slate-900/80 p-2.5 rounded-lg border border-slate-800 text-center space-y-1">
                    <span className="block text-slate-300 text-[11px] truncate">{member.name}</span>
                    <span className={`font-mono font-bold ${member.color}`}>{member.parts} أجزاء</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: INTERACTIVE KIDS STORIES */}
      {activeTab === 'interactiveKids' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Gamepad2 className="w-5 h-5 text-purple-400" />
                القصص الأخلاقية التفاعلية (اختر مسارك بأخلاقك)
              </h3>
              <p className="text-slate-400 text-xs">
                قصص تفاعلية موجهة للأطفال واليافعين، يتوقف فيها السرد عند معضلة أخلاقية ليختار الطفل مساره ويتعلم هدي النبي ﷺ.
              </p>
            </div>

            {/* Scenario switcher */}
            <div className="flex gap-2 text-xs">
              <button
                onClick={() => { setCurrentScenario('honesty'); setStoryStep('start'); }}
                className={`px-3 py-1.5 rounded-lg font-semibold cursor-pointer transition-all ${
                  currentScenario === 'honesty' ? 'bg-purple-600 text-white' : 'bg-slate-950 text-slate-400 border border-slate-800'
                }`}
              >
                موقف الأمانة
              </button>
              <button
                onClick={() => { setCurrentScenario('patience'); setStoryStep('start'); }}
                className={`px-3 py-1.5 rounded-lg font-semibold cursor-pointer transition-all ${
                  currentScenario === 'patience' ? 'bg-purple-600 text-white' : 'bg-slate-950 text-slate-400 border border-slate-800'
                }`}
              >
                موقف كظم الغيظ
              </button>
            </div>
          </div>

          <div className="bg-gradient-to-br from-slate-950 via-purple-950/20 to-slate-950 p-6 rounded-2xl border border-purple-500/30 space-y-6 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <span className="text-xs font-bold text-purple-300 flex items-center gap-1.5">
                <Sparkles className="w-4 h-4 text-amber-400" />
                {currentScenario === 'honesty' ? 'موقف تفاعلي: الأمانة والصدق في التجارة والتعامل' : 'موقف تفاعلي: كظم الغيظ والعفو عند المقدرة'}
              </span>
              <span className="text-[10px] bg-purple-500/20 text-purple-300 px-2.5 py-0.5 rounded-full font-bold">
                للأطفال والجيل الجديد
              </span>
            </div>

            {/* SCENARIO 1: HONESTY */}
            {currentScenario === 'honesty' && (
              <>
                {storyStep === 'start' && (
                  <div className="space-y-6">
                    <p className="text-sm md:text-base text-slate-200 leading-relaxed font-sans">
                      "ذهبت إلى المتجر لتشتري بعض المستلزمات لوالدتك، ودفعت للبائع ورقة نقدية من فئة 50 ريالاً. عند إرجاع الباقي، أخطأ البائع وأعطاك 30 ريالاً بدلاً من 20 ريالاً (زيادة 10 ريالات لم ينتبه لها البائع نهائياً)... ماذا تقرر أن تفعل؟"
                    </p>

                    <div className="grid md:grid-cols-2 gap-4">
                      <button
                        onClick={() => setStoryStep('choiceA')}
                        className="p-5 bg-slate-900 hover:bg-emerald-950/60 border border-slate-700 hover:border-emerald-500/50 rounded-xl text-right transition-all text-xs space-y-1.5 group cursor-pointer"
                      >
                        <span className="font-bold text-emerald-400 block text-sm group-hover:translate-x-1 transition-transform">
                          الخيار (أ): تنبه البائع فوراً وتعيد له الـ 10 ريالات الزائدة.
                        </span>
                        <span className="text-slate-400 text-xs block">التصرف بخلق الصدق والأمانة.</span>
                      </button>

                      <button
                        onClick={() => setStoryStep('choiceB')}
                        className="p-5 bg-slate-900 hover:bg-amber-950/60 border border-slate-700 hover:border-amber-500/50 rounded-xl text-right transition-all text-xs space-y-1.5 group cursor-pointer"
                      >
                        <span className="font-bold text-amber-400 block text-sm group-hover:translate-x-1 transition-transform">
                          الخيار (ب): تأخذ المال وتسكت معتقداً أنه رزق أو هبة جاءت دون طلب.
                        </span>
                        <span className="text-slate-400 text-xs block">الاحتفاظ بالمال الزائد.</span>
                      </button>
                    </div>
                  </div>
                )}

                {storyStep === 'choiceA' && (
                  <div className="bg-emerald-950/40 border border-emerald-500/40 p-5 rounded-xl space-y-3 animate-fade-in">
                    <h4 className="font-bold text-emerald-300 text-base flex items-center gap-2">
                      <Award className="w-5 h-5 text-amber-400" />
                      أحسنت! هذا هو خلق المسلم الحق والاقتداء بسيد المرسلين 🌟
                    </h4>
                    <p className="text-xs text-emerald-100 leading-relaxed">
                      فرح البائع بأمانتك ودعا لك بالبركة والتوفيق. لقد اقتديت بالنبي محمد ﷺ الذي كان يُلقب في قومه قبل البعثة بـ "الصادق الأمين".
                    </p>
                    <div className="bg-emerald-900/40 p-3 rounded-lg border border-emerald-500/30 text-xs text-emerald-200">
                      📖 قال رسول الله ﷺ: "إِنَّ الصِّدْقَ يَهْدِي إِلَى الْبِرِّ، وَإِنَّ الْبِرَّ يَهْدِي إِلَى الْجَنَّةِ".
                    </div>

                    <button
                      onClick={() => setStoryStep('start')}
                      className="mt-3 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs rounded-lg border border-slate-700 cursor-pointer"
                    >
                      إعادة الموقف من البداية
                    </button>
                  </div>
                )}

                {storyStep === 'choiceB' && (
                  <div className="bg-amber-950/40 border border-amber-500/40 p-5 rounded-xl space-y-3 animate-fade-in">
                    <h4 className="font-bold text-amber-300 text-sm">
                      ⚠️ فكّر وتأمل مجدداً... هل يرضى الله بهذا التصرف؟
                    </h4>
                    <p className="text-xs text-amber-100 leading-relaxed">
                      المال الذي أخذته ليس من حقك، والبركة لا تأتي إلا من المال الحلال الطيب. نبينا ﷺ علمنا أن المسلم لا يخون من ائتمنه ولا يأخذ ما لا يحل له.
                    </p>

                    <button
                      onClick={() => setStoryStep('start')}
                      className="mt-3 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs rounded-lg border border-slate-700 cursor-pointer"
                    >
                      حاول مرة أخرى واختر المسار الصحيح
                    </button>
                  </div>
                )}
              </>
            )}

            {/* SCENARIO 2: PATIENCE */}
            {currentScenario === 'patience' && (
              <>
                {storyStep === 'start' && (
                  <div className="space-y-6">
                    <p className="text-sm md:text-base text-slate-200 leading-relaxed font-sans">
                      "أثناء لعبك مع زملائك في فناء المدرسة، صدمك زميلك وأسقط كتابك عمداً وتلفظ بكلمة غير لائقة أمام زملائك... كيف ترد على هذا الموقف؟"
                    </p>

                    <div className="grid md:grid-cols-2 gap-4">
                      <button
                        onClick={() => setStoryStep('choiceA')}
                        className="p-5 bg-slate-900 hover:bg-emerald-950/60 border border-slate-700 hover:border-emerald-500/50 rounded-xl text-right transition-all text-xs space-y-1.5 group cursor-pointer"
                      >
                        <span className="font-bold text-emerald-400 block text-sm group-hover:translate-x-1 transition-transform">
                          الخيار (أ): تكظم غيظك وتبتسم بهدوء وتقول: "سامحك الله وهداك".
                        </span>
                        <span className="text-slate-400 text-xs block">العفو وكظم الغيظ.</span>
                      </button>

                      <button
                        onClick={() => setStoryStep('choiceB')}
                        className="p-5 bg-slate-900 hover:bg-amber-950/60 border border-slate-700 hover:border-amber-500/50 rounded-xl text-right transition-all text-xs space-y-1.5 group cursor-pointer"
                      >
                        <span className="font-bold text-amber-400 block text-sm group-hover:translate-x-1 transition-transform">
                          الخيار (ب): تغضب وترد له الإساءة بإساءة أكبر وتضربه.
                        </span>
                        <span className="text-slate-400 text-xs block">الانتقام والغضب.</span>
                      </button>
                    </div>
                  </div>
                )}

                {storyStep === 'choiceA' && (
                  <div className="bg-emerald-950/40 border border-emerald-500/40 p-5 rounded-xl space-y-3 animate-fade-in">
                    <h4 className="font-bold text-emerald-300 text-base flex items-center gap-2">
                      <Award className="w-5 h-5 text-amber-400" />
                      أنت بطل حقيقي يملك نفسه عند الغضب! 🌟
                    </h4>
                    <p className="text-xs text-emerald-100 leading-relaxed">
                      خجل زميلك من حلمك واعتذر لك فوراً، وكسبت حب الجميع ورضا الله عز وجل.
                    </p>
                    <div className="bg-emerald-900/40 p-3 rounded-lg border border-emerald-500/30 text-xs text-emerald-200">
                      📖 قال تعالى: "وَالْكَاظِمِينَ الْغَيْظَ وَالْعَافِينَ عَنِ النَّاسِ ۗ وَاللَّهُ يُحِبُّ الْمُحْسِنِينَ".
                    </div>

                    <button
                      onClick={() => setStoryStep('start')}
                      className="mt-3 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs rounded-lg border border-slate-700 cursor-pointer"
                    >
                      إعادة الموقف من البداية
                    </button>
                  </div>
                )}

                {storyStep === 'choiceB' && (
                  <div className="bg-amber-950/40 border border-amber-500/40 p-5 rounded-xl space-y-3 animate-fade-in">
                    <h4 className="font-bold text-amber-300 text-sm">
                      ⚠️ الغضب يفقدك حقك ويورث الندم!
                    </h4>
                    <p className="text-xs text-amber-100 leading-relaxed">
                      قال رسول الله ﷺ: "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ". القوة في ضبط النفس وليس في العنف.
                    </p>

                    <button
                      onClick={() => setStoryStep('start')}
                      className="mt-3 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs rounded-lg border border-slate-700 cursor-pointer"
                    >
                      حاول مرة أخرى وسيطر على غضبك
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
