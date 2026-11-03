import React, { useState } from 'react';
import {
  Users,
  MapPin,
  Gamepad2,
  Star,
  CheckCircle,
  Plus,
  ShieldCheck,
  Heart,
  Sparkles,
  ChevronLeft,
  Filter,
  EyeOff,
  Eye,
  Award,
  BookOpen
} from 'lucide-react';

export default function CommunityAndKids() {
  const [activeTab, setActiveTab] = useState('mosqueMap'); // 'mosqueMap', 'groupKhatma', 'interactiveKids'

  // Mosque Map Crowdsourced Data & Filter State
  const [facilityFilter, setFacilityFilter] = useState('all'); // 'all', 'women', 'wheelchair'
  const [mosques, setMosques] = useState([
    {
      id: 1,
      name: 'جامع الراجحي الكبير',
      neighborhood: 'حي القدس',
      distance: '1.2 كم',
      rating: 4.9,
      womenSection: true,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 142
    },
    {
      id: 2,
      name: 'مسجد التوحيد',
      neighborhood: 'حي الروضة',
      distance: '0.5 كم',
      rating: 4.4,
      womenSection: false,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 56
    },
    {
      id: 3,
      name: 'جامع الملك فهد',
      neighborhood: 'حي العليا',
      distance: '2.8 كم',
      rating: 4.8,
      womenSection: true,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 210
    }
  ]);

  // Khatma Group State
  const [khatmaProgress, setKhatmaProgress] = useState(72);
  const [userContribution, setUserContribution] = useState(3); // Parts read
  const [isAnonymous, setIsAnonymous] = useState(true);
  const [khatmaMembers, setKhatmaMembers] = useState([
    { id: 1, name: 'أحمد السعدي', parts: 5, isHidden: false },
    { id: 2, name: 'أبو عبد الرحمن', parts: 4, isHidden: false },
    { id: 3, name: 'فاعل خير (مجهول)', parts: 6, isHidden: true },
    { id: 4, name: 'أنت (مشارك)', parts: 3, isHidden: true },
    { id: 5, name: 'عمر القرني', parts: 4, isHidden: false }
  ]);

  // Interactive Story State
  const [storyScenario, setStoryScenario] = useState('honesty'); // 'honesty', 'forgiveness'
  const [storyStep, setStoryStep] = useState('start'); // 'start', 'choiceA', 'choiceB'

  const stories = {
    honesty: {
      title: 'موقف في السوق: الأمانة والمال الزائد',
      targetAge: 'للأطفال والناشئة (8 - 15 سنة)',
      prompt: 'ذهبت إلى البائع لشراء بعض الفواكه والحلوى للبيت وأعطيته ورقة 50 ريالاً، فأعاد إليك البائع بالخطأ 35 ريالاً بدلاً من 25 ريالاً (أي زيادة 10 ريالات لم ينتبه لها البائع)... ماذا تفعل؟',
      choiceA: {
        text: 'تخبر البائع فوراً بالزيادة وتعيد له الـ 10 ريالات مع ابتسامة.',
        subtext: 'التحلي بخلق الأمانة والصدق.',
        resultTitle: 'أحسنت صنعاً! هذا هو خلق الصادق الأمين ﷺ 🌟',
        resultDesc: 'فرح البائع بأمانتك ودعا لك ولوالديك بالبركة. لقد اقتديت بالنبي محمد ﷺ الذي كان يُعرف قبل البعثة بـ "الصادق الأمين".',
        hadith: 'قال رسول الله ﷺ: "إِنَّ الصِّدْقَ يَهْدِي إِلَى الْبِرِّ وَإِنَّ الْبِرَّ يَهْدِي إِلَى الْجَنَّةِ".'
      },
      choiceB: {
        text: 'تأخذ المال الزائد معتبراً أنه هدية أو حظ سعيد دون إخبار البائع.',
        subtext: 'الاحتفاظ بالمال الذي ليس لك.',
        resultTitle: 'توقف وفكّر مجدداً... هذا مال ليس من حقك ⚠️',
        resultDesc: 'المال الذي يعود بالخطأ هو أمانة يجب ردها. أخذ ما ليس لك يسلب البركة، والمسلم لا يأخذ إلا ما هو حقه بحلال طيب.',
        hadith: 'قال رسول الله ﷺ: "أَدِّ الأَمَانَةَ إِلَى مَنِ ائْتَمَنَكَ وَلا تَخُنْ مَنْ خَانَكَ".'
      }
    },
    forgiveness: {
      title: 'موقف في المدرسة: العفو عند المقدرة والرد بالإحسان',
      targetAge: 'للأطفال والناشئة (7 - 14 سنة)',
      prompt: 'أثناء اللعب في فناء المدرسة، صدمك زميلك وأوقع كتابك في الماء دون قصد لكنه خاف ولم يعتذر فوراً وتجمع الطلاب ينظرون إليك... ماذا تفعل؟',
      choiceA: {
        text: 'تكظم غيظك وتبتسم وتقول له: "لا بأس يا أخي أعلم أنك لم تقصد"، وتساعده على ترتيب الأمر.',
        subtext: 'العفو والإحسان وكظم الغيظ.',
        resultTitle: 'خلق رفيع وأجر عظيم عند الله! 🌿',
        resultDesc: 'العفو في وقت الغضب علامة قوة وليس ضعفاً. خجل زميلك واعتذر فوراً وأصبح صديقاً مخلصاً لك.',
        hadith: 'قال الله تعالى: "وَالْكَاظِمِينَ الْغَيْظَ وَالْعَافِينَ عَنِ النَّاسِ ۗ وَاللَّهُ يُحِبُّ الْمُحْسِنِينَ".'
      },
      choiceB: {
        text: 'تصرخ عليه أمام الطلاب وتدفع كتابه أرضاً للانتقام لنفسك.',
        subtext: 'الانفعال والغضب ومقابلة الخطأ بالخطأ.',
        resultTitle: 'الانتقام يورث الندم والخصومة ⚠️',
        resultDesc: 'الغضب يجعل الشيطان يوقع بين الإخوة. الشديد الحقيقي ليس من يغلب الناس بيده، بل من يملك نفسه عند الغضب.',
        hadith: 'قال رسول الله ﷺ: "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ".'
      }
    }
  };

  const filteredMosques = mosques.filter(m => {
    if (facilityFilter === 'women') return m.womenSection;
    if (facilityFilter === 'wheelchair') return m.wheelchairAccess;
    return true;
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-purple-950/80 via-emerald-900/50 to-slate-900 border border-purple-500/30 rounded-3xl p-6 md:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-purple-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-purple-500/20 text-purple-300 text-xs font-semibold mb-3 border border-purple-500/30">
              <Users className="w-3.5 h-3.5" />
              العمل التشاركي والأجيال الصاعدة
            </div>
            <h2 className="text-2xl md:text-3xl font-extrabold text-white">المجتمع والجيل الجديد</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              خريطة مرافق المساجد التشاركية، غرف الختمات العائلية المحمية من الرياء، وقصص تفاعلية تزرع الأخلاق النبوية في الأطفال.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/80 p-1.5 rounded-2xl border border-slate-800">
            <button
              onClick={() => setActiveTab('mosqueMap')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'mosqueMap' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              خريطة المساجد
            </button>
            <button
              onClick={() => setActiveTab('groupKhatma')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'groupKhatma' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الختمات التنافسية
            </button>
            <button
              onClick={() => setActiveTab('interactiveKids')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
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
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <MapPin className="w-5 h-5 text-purple-400" />
                خريطة مرافق المساجد التشاركية (Crowdsourced Mosque Map)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                تحديث وتقييم حالة المساجد من روادها: مصليات السيدات، كراسي كبار السن، والنظافة وتيسير الوصول.
              </p>
            </div>

            {/* Filter buttons */}
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400">تصفية:</span>
              <button
                onClick={() => setFacilityFilter('all')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
                  facilityFilter === 'all' ? 'bg-purple-600 text-white border-purple-500' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                الكل
              </button>
              <button
                onClick={() => setFacilityFilter('women')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
                  facilityFilter === 'women' ? 'bg-purple-600 text-white border-purple-500' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                مصلى سيدات
              </button>
              <button
                onClick={() => setFacilityFilter('wheelchair')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
                  facilityFilter === 'wheelchair' ? 'bg-purple-600 text-white border-purple-500' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                مدخل ذوي الهمم
              </button>
            </div>
          </div>

          <div className="grid md:grid-cols-3 gap-4">
            {filteredMosques.map((mosque) => (
              <div
                key={mosque.id}
                className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-4 hover:border-purple-500/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <h4 className="font-bold text-white text-base">{mosque.name}</h4>
                      <span className="text-xs text-slate-400">{mosque.neighborhood} • يبعد {mosque.distance}</span>
                    </div>
                    <div className="flex items-center gap-1 bg-amber-500/20 text-amber-300 px-2.5 py-1 rounded-full text-xs font-bold border border-amber-500/30">
                      <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                      {mosque.rating} ({mosque.reviewsCount})
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-2 text-xs pt-3">
                    <div className={`p-2 rounded-xl flex items-center gap-1.5 ${mosque.womenSection ? 'bg-emerald-950/40 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900/50 text-slate-500 border border-slate-800'}`}>
                      <CheckCircle className="w-3.5 h-3.5" />
                      مصلى سيدات
                    </div>
                    <div className={`p-2 rounded-xl flex items-center gap-1.5 ${mosque.cleanWashrooms ? 'bg-emerald-950/40 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900/50 text-slate-500 border border-slate-800'}`}>
                      <CheckCircle className="w-3.5 h-3.5" />
                      وضوء ونظافة
                    </div>
                    <div className={`p-2 rounded-xl flex items-center gap-1.5 ${mosque.wheelchairAccess ? 'bg-emerald-950/40 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900/50 text-slate-500 border border-slate-800'}`}>
                      <CheckCircle className="w-3.5 h-3.5" />
                      مدخل عربات
                    </div>
                    <div className={`p-2 rounded-xl flex items-center gap-1.5 ${mosque.elderlyChairs ? 'bg-emerald-950/40 text-emerald-300 border border-emerald-500/20' : 'bg-slate-900/50 text-slate-500 border border-slate-800'}`}>
                      <CheckCircle className="w-3.5 h-3.5" />
                      كراسي كبار السن
                    </div>
                  </div>
                </div>

                <button className="w-full py-2 bg-slate-900 hover:bg-slate-800 text-slate-300 text-xs font-semibold rounded-xl border border-slate-800 transition-colors">
                  تحديث التقييم والمرافق
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 2: GROUP KHATMA */}
      {activeTab === 'groupKhatma' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Users className="w-5 h-5 text-purple-400" />
                ختمات وتحديات العائلة والأصدقاء (Private Groups)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                غرف خاصة تتيح تقاسم قراءة القرآن والورد اليومي مع حماية الإخلاص وميزة إخفاء الأسماء لمنع الرياء.
              </p>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setIsAnonymous(!isAnonymous)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold border flex items-center gap-1.5 transition-all ${
                  isAnonymous ? 'bg-emerald-600/20 text-emerald-300 border-emerald-500/40' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                {isAnonymous ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
                {isAnonymous ? 'وضع الخصوصية (إخفاء الاسم مفعّل)' : 'الاسم ظاهر'}
              </button>
            </div>
          </div>

          <div className="bg-slate-950 p-6 rounded-3xl border border-slate-800 space-y-6">
            <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-300 font-bold">
                <span>نسبة إنجاز الختمة الجماعية:</span>
                <span className="text-purple-400 font-mono">{khatmaProgress}% (21.5 جزء مكتمل)</span>
              </div>
              <div className="w-full bg-slate-900 h-3 rounded-full overflow-hidden border border-slate-800">
                <div
                  className="bg-gradient-to-r from-purple-600 via-teal-500 to-emerald-500 h-full transition-all duration-500"
                  style={{ width: `${khatmaProgress}%` }}
                ></div>
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              <div className="bg-slate-900 p-5 rounded-2xl border border-slate-800 space-y-3">
                <span className="text-xs font-bold text-purple-300 block">لوحة أعضاء الغرفة وإنجازهم:</span>
                <div className="space-y-2 text-xs">
                  {khatmaMembers.map(member => (
                    <div key={member.id} className="flex justify-between items-center p-2.5 bg-slate-950 rounded-xl border border-slate-800">
                      <span className="text-slate-200">
                        {member.isHidden ? 'عضو كريم (مستور)' : member.name}
                      </span>
                      <span className="text-emerald-400 font-bold font-mono">{member.parts} أجزاء</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="bg-slate-900 p-5 rounded-2xl border border-slate-800 flex flex-col justify-between space-y-4">
                <div className="space-y-2">
                  <span className="text-xs font-bold text-emerald-400 block">مساهمتك في ختمة اليوم:</span>
                  <div className="text-3xl font-black text-white font-mono">
                    {userContribution} <span className="text-xs font-sans text-slate-400">أجزاء مكتملة</span>
                  </div>
                  <p className="text-[11px] text-slate-400 leading-relaxed">
                    قراءة الجزء تأخذ في المتوسط 20 دقيقة، وتلاوتك تدعم أفراد عائلتك في إتمام الختمة أسرع.
                  </p>
                </div>

                <button
                  onClick={() => {
                    setUserContribution(userContribution + 1);
                    setKhatmaProgress(Math.min(100, khatmaProgress + 3));
                  }}
                  className="w-full py-3 bg-purple-600 hover:bg-purple-500 text-white font-bold rounded-xl transition-all shadow-lg shadow-purple-950 text-xs flex items-center justify-center gap-1.5"
                >
                  <Plus className="w-4 h-4" />
                  تسجيل إتمام جزء جديد الآن
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: INTERACTIVE KIDS STORIES */}
      {activeTab === 'interactiveKids' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Gamepad2 className="w-5 h-5 text-purple-400" />
                القصص الأخلاقية التفاعلية (اختر مسارك بأخلاقك)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                سرد تفاعلي شيق يتوقف عند مواقف سلوكية ويطلب من الطفل اتخاذ القرار ليرى العاقبة وهدي النبي ﷺ.
              </p>
            </div>

            <div className="flex gap-2">
              <button
                onClick={() => {
                  setStoryScenario('honesty');
                  setStoryStep('start');
                }}
                className={`px-3 py-1.5 text-xs font-bold rounded-xl border transition-all ${
                  storyScenario === 'honesty' ? 'bg-purple-600 text-white border-purple-500' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                موقف الأمانة في السوق
              </button>
              <button
                onClick={() => {
                  setStoryScenario('forgiveness');
                  setStoryStep('start');
                }}
                className={`px-3 py-1.5 text-xs font-bold rounded-xl border transition-all ${
                  storyScenario === 'forgiveness' ? 'bg-purple-600 text-white border-purple-500' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                موقف العفو في المدرسة
              </button>
            </div>
          </div>

          <div className="bg-gradient-to-br from-slate-950 via-purple-950/20 to-slate-950 p-6 md:p-8 rounded-3xl border border-purple-500/30 space-y-6 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <span className="text-xs font-bold text-purple-300 flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-amber-400" />
                {stories[storyScenario].title}
              </span>
              <span className="text-[10px] bg-purple-500/20 text-purple-300 px-3 py-1 rounded-full font-bold">
                {stories[storyScenario].targetAge}
              </span>
            </div>

            {storyStep === 'start' && (
              <div className="space-y-6">
                <p className="text-base text-slate-200 leading-relaxed font-sans bg-slate-900/60 p-5 rounded-2xl border border-slate-800">
                  "{stories[storyScenario].prompt}"
                </p>

                <div className="grid md:grid-cols-2 gap-4">
                  <button
                    onClick={() => setStoryStep('choiceA')}
                    className="p-5 bg-slate-900 hover:bg-emerald-950/60 border border-slate-700 hover:border-emerald-500/50 rounded-2xl text-right transition-all space-y-2 group"
                  >
                    <span className="font-bold text-emerald-400 block group-hover:translate-x-1 transition-transform text-sm">
                      الخيار (أ): {stories[storyScenario].choiceA.text}
                    </span>
                    <span className="text-slate-400 text-xs block">{stories[storyScenario].choiceA.subtext}</span>
                  </button>

                  <button
                    onClick={() => setStoryStep('choiceB')}
                    className="p-5 bg-slate-900 hover:bg-amber-950/60 border border-slate-700 hover:border-amber-500/50 rounded-2xl text-right transition-all space-y-2 group"
                  >
                    <span className="font-bold text-amber-400 block group-hover:translate-x-1 transition-transform text-sm">
                      الخيار (ب): {stories[storyScenario].choiceB.text}
                    </span>
                    <span className="text-slate-400 text-xs block">{stories[storyScenario].choiceB.subtext}</span>
                  </button>
                </div>
              </div>
            )}

            {storyStep === 'choiceA' && (
              <div className="bg-emerald-950/40 border border-emerald-500/40 p-6 rounded-2xl space-y-4 animate-fade-in">
                <h4 className="font-bold text-emerald-300 text-base flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-emerald-400" />
                  {stories[storyScenario].choiceA.resultTitle}
                </h4>
                <p className="text-xs text-emerald-100 leading-relaxed">
                  {stories[storyScenario].choiceA.resultDesc}
                </p>
                <div className="p-3 bg-slate-950/60 rounded-xl border border-emerald-500/30 text-xs text-emerald-300 font-serif">
                  {stories[storyScenario].choiceA.hadith}
                </div>

                <button
                  onClick={() => setStoryStep('start')}
                  className="mt-4 px-5 py-2.5 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs font-bold rounded-xl border border-slate-700 transition-colors"
                >
                  تجربة اختيار آخر أو إعادة الموقف
                </button>
              </div>
            )}

            {storyStep === 'choiceB' && (
              <div className="bg-amber-950/40 border border-amber-500/40 p-6 rounded-2xl space-y-4 animate-fade-in">
                <h4 className="font-bold text-amber-300 text-base">
                  {stories[storyScenario].choiceB.resultTitle}
                </h4>
                <p className="text-xs text-amber-100 leading-relaxed">
                  {stories[storyScenario].choiceB.resultDesc}
                </p>
                <div className="p-3 bg-slate-950/60 rounded-xl border border-amber-500/30 text-xs text-amber-300 font-serif">
                  {stories[storyScenario].choiceB.hadith}
                </div>

                <button
                  onClick={() => setStoryStep('start')}
                  className="mt-4 px-5 py-2.5 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs font-bold rounded-xl border border-slate-700 transition-colors"
                >
                  حاول مرة أخرى واختر المسار النبوي الصحيح
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
