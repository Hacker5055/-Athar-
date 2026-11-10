import React, { useState } from 'react';
import { Users, MapPin, Gamepad2, Star, CheckCircle, Plus, ShieldCheck, Heart, Sparkles, ChevronLeft, Award, HelpCircle } from 'lucide-react';

export default function CommunityAndKids() {
  const [activeTab, setActiveTab] = useState('mosqueMap'); // 'mosqueMap', 'groupKhatma', 'interactiveKids'

  // Mosque Map Crowdsourced Data
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
      reviewsCount: 128
    },
    {
      id: 2,
      name: 'مسجد التوحيد',
      neighborhood: 'حي النخيل',
      distance: '0.5 كم',
      rating: 4.2,
      womenSection: false,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 45
    },
    {
      id: 3,
      name: 'جامع الملك فهد',
      neighborhood: 'حي الورود',
      distance: '2.4 كم',
      rating: 4.8,
      womenSection: true,
      cleanWashrooms: true,
      wheelchairAccess: true,
      elderlyChairs: true,
      reviewsCount: 94
    }
  ]);

  const [showAddReviewModal, setShowAddReviewModal] = useState(false);
  const [selectedMosqueForReview, setSelectedMosqueForReview] = useState(1);

  // Khatma Group State
  const [khatmaProgress, setKhatmaProgress] = useState(68);
  const [userContribution, setUserContribution] = useState(2); // Parts read
  const [isAnonymous, setIsAnonymous] = useState(true);

  // Interactive Stories State
  const [selectedStory, setSelectedStory] = useState('amana'); // 'amana', 'sidq', 'birr'
  const [storyStep, setStoryStep] = useState('start'); // 'start', 'choiceA', 'choiceB'

  const storiesData = {
    amana: {
      title: 'موقف في السوق: الأمانة وبركة الرزق',
      character: 'أحمد الصغير في متجر الحي',
      situation: 'ذهبت لشراء بعض المستلزمات وأعطيت البائع 50 ريالاً، فرد لك البائع 30 ريالاً بالخطأ بدلاً من 20 ريالاً (زيادة 10 ريالات لم يلتفت إليها)... ماذا تفعل؟',
      choiceA: {
        text: 'تعيد الـ 10 ريالات فوراً للبائع وتخبره بالخطأ وتشكره.',
        resultTitle: 'تصرف نبيل ومبارك! كسبت خلق الأمانة 🌟',
        resultDesc: 'فرح البائع بصدقك ودعا لك بالبركة. لقد اقتديت بالنبي ﷺ الذي لُقب قبل البعثة بـ "الصادق الأمين".',
        hadith: 'قال رسول الله ﷺ: "أَدِّ الأَمَانَةَ إِلَى مَنِ ائْتَمَنَكَ وَلا تَخُنْ مَنْ خَانَكَ".'
      },
      choiceB: {
        text: 'تأخذ المال سريعاً وتفرح وتظن أنها رزق جاء دون طلب.',
        resultTitle: '⚠️ فكّر مجدداً... هل هذا مال حلال؟',
        resultDesc: 'أخذ ما ليس لك بغير حق لا بركة فيه ويمحق راحة البال. الصدق والأمانة في الدرهم القليل هما اختبار المعدن الحقيقي للمؤمن.'
      }
    },
    birr: {
      title: 'موقف في البيت: بر الوالدين وتقديم الرضا',
      character: 'عمر وألعاب الفيديو المسائية',
      situation: 'كنت تلعب لعبتك المفضلة مع أصدقائك عبر الإنترنت في مرحلة حاسمة، فنادتك والدتك لمساعدتها في حمل بعض الأغراض من السيارة... ماذا تختار؟',
      choiceA: {
        text: 'تستجيب فوراً لوالدتك وتبتسم وتعتذر لأصدقائك لدقائق معدودة.',
        resultTitle: 'أحسنت! هذا هو البر الذي يفتح أبواب الجنة 🌺',
        resultDesc: 'نلت دعوة صالحة من والدتك، ورضا الوالدين من أعظم أسباب التوفيق في الدراسة والحياة.',
        hadith: 'قال النبي ﷺ لما سُئل: أي العمل أحب إلى الله؟ قال: "الصَّلاَةُ عَلَى وَقْتِهَا، ثُمَّ بِرُّ الْوَالِدَيْنِ".'
      },
      choiceB: {
        text: 'تتجاهل النداء وتكمل الجولة بحجة أنك لا تستطيع إيقاف اللعبة.',
        resultTitle: '⚠️ انتبه... الوالدان أولى من كل لهو!',
        resultDesc: 'إغضاب الوالدين أو التأفف في وجههما يضيع حسنات كثيرة. تذكر أن وقوفك بجانبهما عبادة تؤجر عليها.'
      }
    }
  };

  const handleUpdateMosqueFacility = (mosqueId, facility) => {
    setMosques(mosques.map(m => m.id === mosqueId ? { ...m, [facility]: !m[facility] } : m));
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
              التفاعل المجتمعي والأجيال الناشئة
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المجتمع والجيل الجديد</h2>
            <p className="text-slate-300 text-sm mt-1">
              خريطة مرافق المساجد التفاعلية، الختمات والتحديات الجماعية بدون رياء، والقصص الأخلاقية التفاعلية للأطفال.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('mosqueMap')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'mosqueMap' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              خريطة المساجد
            </button>
            <button
              onClick={() => setActiveTab('groupKhatma')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'groupKhatma' ? 'bg-purple-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الختمات التنافسية
            </button>
            <button
              onClick={() => setActiveTab('interactiveKids')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
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
                تحديث وتقييم خدمات المساجد من المصلين (مصلى السيدات، نظافة الوضوء، مدخل ذوي الاحتياجات الخاصة).
              </p>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400 bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800">
                📍 الرياض - النطاق القريب
              </span>
            </div>
          </div>

          <div className="grid md:grid-cols-3 gap-4">
            {mosques.map((mosque) => (
              <div key={mosque.id} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-4 hover:border-purple-500/40 transition-all flex flex-col justify-between">
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
                    <button
                      onClick={() => handleUpdateMosqueFacility(mosque.id, 'womenSection')}
                      className={`p-2 rounded-lg flex items-center justify-between transition-colors ${
                        mosque.womenSection ? 'bg-emerald-950/60 text-emerald-300 border border-emerald-500/30' : 'bg-slate-900 text-slate-500 border border-slate-800'
                      }`}
                    >
                      <span className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5" /> مصلى سيدات</span>
                      <span className="text-[10px] text-slate-400">تعديل</span>
                    </button>

                    <button
                      onClick={() => handleUpdateMosqueFacility(mosque.id, 'cleanWashrooms')}
                      className={`p-2 rounded-lg flex items-center justify-between transition-colors ${
                        mosque.cleanWashrooms ? 'bg-emerald-950/60 text-emerald-300 border border-emerald-500/30' : 'bg-slate-900 text-slate-500 border border-slate-800'
                      }`}
                    >
                      <span className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5" /> نظافة الوضوء</span>
                      <span className="text-[10px] text-slate-400">تعديل</span>
                    </button>

                    <button
                      onClick={() => handleUpdateMosqueFacility(mosque.id, 'wheelchairAccess')}
                      className={`p-2 rounded-lg flex items-center justify-between transition-colors ${
                        mosque.wheelchairAccess ? 'bg-emerald-950/60 text-emerald-300 border border-emerald-500/30' : 'bg-slate-900 text-slate-500 border border-slate-800'
                      }`}
                    >
                      <span className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5" /> مدخل همم</span>
                      <span className="text-[10px] text-slate-400">تعديل</span>
                    </button>

                    <button
                      onClick={() => handleUpdateMosqueFacility(mosque.id, 'elderlyChairs')}
                      className={`p-2 rounded-lg flex items-center justify-between transition-colors ${
                        mosque.elderlyChairs ? 'bg-emerald-950/60 text-emerald-300 border border-emerald-500/30' : 'bg-slate-900 text-slate-500 border border-slate-800'
                      }`}
                    >
                      <span className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5" /> كراسي كبار سن</span>
                      <span className="text-[10px] text-slate-400">تعديل</span>
                    </button>
                  </div>
                </div>

                <div className="pt-3 border-t border-slate-800/80 flex justify-between items-center text-[11px] text-slate-400">
                  <span>آخر تقييم: قبل ساعتين</span>
                  <span className="text-purple-400 font-semibold cursor-pointer hover:underline">مراجعة الملاحظات</span>
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
                ختمات وتحديات العائلة والأصدقاء (Private Challenge Rooms)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                متابعة تحفيزية للورد اليومي وختمة القرآن الجماعية دون رياء (إمكانية إخفاء الأسماء للحفاظ على الإخلاص).
              </p>
            </div>
            <div className="flex items-center gap-3">
              <button
                onClick={() => setIsAnonymous(!isAnonymous)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
                  isAnonymous ? 'bg-purple-600/20 text-purple-300 border-purple-500/40' : 'bg-slate-950 text-slate-400 border-slate-800'
                }`}
              >
                {isAnonymous ? 'وضع الخصوصية: مفعّل (اسم مستعار)' : 'وضع الخصوصية: معطّل (اسمك الحقيقي)'}
              </button>
            </div>
          </div>

          <div className="bg-slate-950 p-6 rounded-2xl border border-slate-800 space-y-6">
            <div className="space-y-2">
              <div className="flex justify-between text-xs text-slate-300 font-bold">
                <span>نسبة إنجاز ختمة العائلة المباركة 🌙:</span>
                <span className="text-purple-400 font-mono">{khatmaProgress}% (20.5 جزء مكتمل)</span>
              </div>
              <div className="w-full bg-slate-900 h-3 rounded-full overflow-hidden border border-slate-800">
                <div className="bg-gradient-to-r from-purple-600 to-emerald-500 h-full transition-all duration-500" style={{ width: `${khatmaProgress}%` }}></div>
              </div>
            </div>

            <div className="grid md:grid-cols-3 gap-4 text-xs">
              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400">مساهمتك في الختمة:</span>
                <p className="text-lg font-bold text-emerald-400">{userContribution} أجزاء مقروءة</p>
              </div>

              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-1">
                <span className="text-slate-400">هويتك في الغرفة:</span>
                <p className="text-sm font-bold text-purple-300 flex items-center gap-1">
                  <ShieldCheck className="w-4 h-4 text-purple-400" />
                  {isAnonymous ? 'عضو عائلي مستور (عضو #4)' : 'عبدالله بن محمد'}
                </p>
              </div>

              <div className="bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-1 flex flex-col justify-center">
                <button
                  onClick={() => {
                    setUserContribution(userContribution + 1);
                    setKhatmaProgress(Math.min(100, khatmaProgress + 3.3));
                  }}
                  className="w-full py-2.5 bg-purple-600 hover:bg-purple-500 text-white font-bold rounded-lg transition-all shadow-md"
                >
                  تسجيل قراءة جزء جديد 📖
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: INTERACTIVE KIDS STORIES */}
      {activeTab === 'interactiveKids' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-3">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Gamepad2 className="w-5 h-5 text-purple-400" />
                القصص الأخلاقية التفاعلية (اختر مسارك بأخلاقك)
              </h3>
              <p className="text-slate-400 text-xs">
                قصص تفاعلية متفرعة للأطفال والشباب يتخذون فيها قرارات أخلاقية ويتعلمون الاقتداء بسيرة النبي ﷺ.
              </p>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400">اختر القصة:</span>
              <select
                value={selectedStory}
                onChange={(e) => { setSelectedStory(e.target.value); setStoryStep('start'); }}
                className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-xl px-3 py-2 focus:outline-none focus:border-purple-500"
              >
                <option value="amana">الأمانة في المعاملة والبيع</option>
                <option value="birr">بر الوالدين وأولويات الحياة</option>
              </select>
            </div>
          </div>

          <div className="bg-gradient-to-br from-slate-950 via-purple-950/20 to-slate-950 p-6 rounded-2xl border border-purple-500/30 space-y-6 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <span className="text-xs font-bold text-purple-300 flex items-center gap-1.5">
                <Sparkles className="w-4 h-4 text-amber-400" />
                {storiesData[selectedStory].title}
              </span>
              <span className="text-[10px] bg-purple-500/20 text-purple-300 px-2.5 py-0.5 rounded-full">
                {storiesData[selectedStory].character}
              </span>
            </div>

            {storyStep === 'start' && (
              <div className="space-y-6">
                <p className="text-sm text-slate-200 leading-relaxed font-sans">
                  "{storiesData[selectedStory].situation}"
                </p>

                <div className="grid md:grid-cols-2 gap-4">
                  <button
                    onClick={() => setStoryStep('choiceA')}
                    className="p-4 bg-slate-900 hover:bg-emerald-950/60 border border-slate-700 hover:border-emerald-500/50 rounded-xl text-right transition-all text-xs space-y-1.5 group"
                  >
                    <span className="font-bold text-emerald-400 block group-hover:translate-x-1 transition-transform">
                      الخيار (أ): {storiesData[selectedStory].choiceA.text}
                    </span>
                    <span className="text-slate-400 text-[11px]">اتباع الهدي والضمير الصالح.</span>
                  </button>

                  <button
                    onClick={() => setStoryStep('choiceB')}
                    className="p-4 bg-slate-900 hover:bg-amber-950/60 border border-slate-700 hover:border-amber-500/50 rounded-xl text-right transition-all text-xs space-y-1.5 group"
                  >
                    <span className="font-bold text-amber-400 block group-hover:translate-x-1 transition-transform">
                      الخيار (ب): {storiesData[selectedStory].choiceB.text}
                    </span>
                    <span className="text-slate-400 text-[11px]">اختيار المسار السهل دون تفكير بالعواقب.</span>
                  </button>
                </div>
              </div>
            )}

            {storyStep === 'choiceA' && (
              <div className="bg-emerald-950/40 border border-emerald-500/40 p-5 rounded-xl space-y-3 animate-fade-in">
                <h4 className="font-bold text-emerald-300 text-sm flex items-center gap-1.5">
                  <CheckCircle className="w-5 h-5" />
                  {storiesData[selectedStory].choiceA.resultTitle}
                </h4>
                <p className="text-xs text-emerald-100 leading-relaxed">
                  {storiesData[selectedStory].choiceA.resultDesc}
                </p>
                <p className="text-[11px] text-emerald-300 font-semibold pt-1 border-t border-emerald-500/20">
                  {storiesData[selectedStory].choiceA.hadith}
                </p>

                <button
                  onClick={() => setStoryStep('start')}
                  className="mt-3 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs rounded-lg border border-slate-700"
                >
                  إعادة الموقف أو تجربة خيار آخر
                </button>
              </div>
            )}

            {storyStep === 'choiceB' && (
              <div className="bg-amber-950/40 border border-amber-500/40 p-5 rounded-xl space-y-3 animate-fade-in">
                <h4 className="font-bold text-amber-300 text-sm">
                  {storiesData[selectedStory].choiceB.resultTitle}
                </h4>
                <p className="text-xs text-amber-100 leading-relaxed">
                  {storiesData[selectedStory].choiceB.resultDesc}
                </p>

                <button
                  onClick={() => setStoryStep('start')}
                  className="mt-3 px-4 py-2 bg-slate-900 hover:bg-slate-800 text-slate-200 text-xs rounded-lg border border-slate-700"
                >
                  حاول مجدداً واختر المسار الصحيح
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
