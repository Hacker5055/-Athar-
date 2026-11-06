import React, { useState } from 'react';
import { Mic, MicOff, Volume2, CheckCircle2, AlertCircle, RefreshCw, BookOpen, Brain, Sparkles, ChevronLeft, ChevronRight, Layers, Play } from 'lucide-react';

export default function QuranMemorization() {
  const [activeTab, setActiveTab] = useState('corrector'); // 'corrector', 'spaced', 'mindmap'

  // Audio Reciter State
  const [isRecording, setIsRecording] = useState(false);
  const [recordingResult, setRecordingResult] = useState(null);
  const [selectedAyah, setSelectedAyah] = useState({
    surah: 'سورة الملك',
    ayahNum: 1,
    expectedText: 'تَبَٰرَكَ ٱلَّذِي بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ'
  });

  // Spaced Repetition State
  const [flashcards, setFlashcards] = useState([
    { id: 1, surah: 'سورة الملك', ayats: '1 - 5', level: 'يحتاج مراجعة قريبة', interval: 'بعد 6 ساعات', progress: 40, status: 'warning', repetitions: 3 },
    { id: 2, surah: 'سورة الكهف', ayats: '10 - 20', level: 'مستقر نسبياً', interval: 'غداً', progress: 75, status: 'info', repetitions: 6 },
    { id: 3, surah: 'سورة مريم', ayats: '1 - 15', level: 'ممتاز - حفظ متقن', interval: 'بعد 5 أيام', progress: 95, status: 'success', repetitions: 12 },
    { id: 4, surah: 'سورة يس', ayats: '1 - 12', level: 'جديد - قيد التثبيت', interval: 'اليوم', progress: 20, status: 'warning', repetitions: 1 },
  ]);

  const [activeReviewCard, setActiveReviewCard] = useState(null);

  // Mind Map State
  const [selectedSurah, setSelectedSurah] = useState('الملك');

  const handleStartRecording = () => {
    setIsRecording(true);
    setRecordingResult(null);
    setTimeout(() => {
      setIsRecording(false);
      setRecordingResult({
        text: "تَبَٰرَكَ ٱلَّذِي بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
        tajweedNote: "تنبيه تجويدي: انتبه لمقدار الغنة في الإخفاء الحقيقي عند القاف في (شَيْءٍ قَدِيرٌ)، القاف حرف استعلاء مفخم.",
        accuracy: 94,
        details: [
          { rule: "إخفاء حقيقي", position: "شَيْءٍ قَدِيرٌ", status: "تنبه للتفخيم" },
          { rule: "مد طبيعي", position: "ٱلَّذِي", status: "سليم (حركتان)" },
          { rule: "مد عارض للسكون", position: "قَدِيرٌ", status: "ممتاز (4 حركات)" }
        ]
      });
    }, 2500);
  };

  const handleReviewFeedback = (cardId, rating) => {
    // rating: 'hard', 'good', 'easy'
    setFlashcards(flashcards.map(c => {
      if (c.id === cardId) {
        if (rating === 'easy') {
          return { ...c, progress: Math.min(100, c.progress + 15), interval: 'بعد 7 أيام', status: 'success', level: 'ممتاز - حفظ راسخ' };
        } else if (rating === 'good') {
          return { ...c, progress: Math.min(100, c.progress + 5), interval: 'بعد 3 أيام', status: 'info', level: 'مستقر' };
        } else {
          return { ...c, progress: Math.max(10, c.progress - 15), interval: 'خلال 4 ساعات', status: 'warning', level: 'يحتاج إعادة وتكرار' };
        }
      }
      return c;
    }));
    setActiveReviewCard(null);
  };

  const surahMindMaps = {
    'الملك': {
      title: 'سورة الملك (المنجية) - خريطة المحاور الموضوعية',
      description: 'سورة مكية تعنى بإثبات كمال قدرة الله ونفاذ مشيئته، وبيان حقيقة الحياة والموت وعاقبة الجاحدين.',
      topics: [
        { title: 'عظمة القدرة الإلهية وخلق الموت والحياة', range: 'الآيات 1 - 5', bg: 'from-emerald-950/80 to-emerald-900/40', border: 'border-emerald-500/40', keywords: 'الملك، العزيز الغفور، سبع سماوات، رجوماً للشياطين' },
        { title: 'عاقبة المكذبين ومصير أصحاب السعير', range: 'الآيات 6 - 12', bg: 'from-amber-950/80 to-amber-900/40', border: 'border-amber-500/40', keywords: 'شهيقاً وتفور، تكاد تميز من الغيظ، فاعترفوا بذنبهم' },
        { title: 'شمول علم الله وبديع خلقه للأرض والسماء', range: 'الآيات 13 - 19', bg: 'from-blue-950/80 to-blue-900/40', border: 'border-blue-500/40', keywords: 'بذات الصدور، جعل لكم الأرض ذلولاً، أأمنتم من في السماء' },
        { title: 'التذكير بالنعم وتحذير العباد من العذاب', range: 'الآيات 20 - 30', bg: 'from-purple-950/80 to-purple-900/40', border: 'border-purple-500/40', keywords: 'جند ينصركم، السمع والأبصار والأفئدة، قل أرأيتم إن أصبح ماؤكم غوراً' },
      ]
    },
    'الكهف': {
      title: 'سورة الكهف - خريطة الفتن الأربع وقوارب النجاة منها',
      description: 'سورة مكية تمثل دستوراً للمسلم في مواجهة فتن الزمان عبر 4 قصص رئيسية ومحاورها الأخلاقية.',
      topics: [
        { title: 'فتنة الدين - قصة أصحاب الكهف (النجاة: الصحبة الصالحة)', range: 'الآيات 9 - 26', bg: 'from-emerald-950/80 to-emerald-900/40', border: 'border-emerald-500/40', keywords: 'الفتية، الرقيم، الكلب باسط ذراعيه، ثلاث مائة سنين' },
        { title: 'فتنة المال - قصة صاحب الجنتين (النجاة: إدراك زوال الدنيا)', range: 'الآيات 32 - 44', bg: 'from-amber-950/80 to-amber-900/40', border: 'border-amber-500/40', keywords: 'جنتين من أعناب، ما أظن أن تبيد، ما شاء الله لا قوة إلا بالله' },
        { title: 'فتنة العلم - قصة موسى والخضر (النجاة: التواضع وطلب الحكمة)', range: 'الآيات 60 - 82', bg: 'from-blue-950/80 to-blue-900/40', border: 'border-blue-500/40', keywords: 'مجمع البحرين، خرق السفينة، قتل الغلام، بناء الجدار' },
        { title: 'فتنة السلطة - قصة ذي القرنين (النجاة: العدل والإخلاص)', range: 'الآيات 83 - 98', bg: 'from-purple-950/80 to-purple-900/40', border: 'border-purple-500/40', keywords: 'مكنّا له في الأرض، بين السدين، ردم ياجوج وماجوج' },
      ]
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-emerald-900/80 via-emerald-800/50 to-slate-900 border border-emerald-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-semibold mb-3 border border-emerald-500/30">
              <Sparkles className="w-3.5 h-3.5" />
              الذكاء الاصطناعي التجويدي والحفظ التكراري
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">الحفظ والمراجعة الذكية</h2>
            <p className="text-slate-300 text-sm mt-1">
              مصحح صوتي ذكي للمخارج والأحكام، مراجعة مبنية على خوارزميات التكرار المتباعد، وخرائط ذهنية لربط السور.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('corrector')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'corrector' ? 'bg-emerald-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              مصحح التلاوة
            </button>
            <button
              onClick={() => setActiveTab('spaced')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'spaced' ? 'bg-emerald-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              التكرار المتباعد
            </button>
            <button
              onClick={() => setActiveTab('mindmap')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'mindmap' ? 'bg-emerald-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الخرائط الذهنية
            </button>
          </div>
        </div>
      </div>

      {/* TAB 1: AI CORRECTIONS */}
      {activeTab === 'corrector' && (
        <div className="grid md:grid-cols-2 gap-6">
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 flex flex-col justify-between shadow-lg">
            <div>
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
                  <Mic className="w-5 h-5" />
                  مستمع التلاوة ومحلل التجويد
                </h3>
                <span className="text-xs bg-slate-800 text-slate-300 px-2.5 py-1 rounded-full border border-slate-700">
                  {selectedAyah.surah} - آية {selectedAyah.ayahNum}
                </span>
              </div>

              <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-center mb-6">
                <span className="text-[11px] text-slate-400 block mb-1">الآية المطلوب تلاوتها وتسميعها:</span>
                <p className="text-base md:text-lg font-serif text-slate-200 leading-relaxed font-bold">
                  "{selectedAyah.expectedText}"
                </p>
              </div>

              {/* Record Button UI */}
              <div className="flex flex-col items-center justify-center my-6">
                <button
                  onClick={handleStartRecording}
                  disabled={isRecording}
                  className={`relative group p-6 rounded-full transition-all duration-300 cursor-pointer ${
                    isRecording
                      ? 'bg-red-500/20 text-red-400 border-2 border-red-500 animate-pulse'
                      : 'bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-400 border-2 border-emerald-500/50 hover:scale-105'
                  }`}
                >
                  {isRecording ? <MicOff className="w-10 h-10 animate-bounce" /> : <Mic className="w-10 h-10" />}
                </button>
                <p className="text-xs text-slate-400 mt-4">
                  {isRecording ? "جاري الاستماع للترتيل وتحليل التشكيل والأحكام..." : "انقر على الميكروفون لبدء التسميع الصوتي"}
                </p>
              </div>
            </div>

            <div className="bg-slate-950/70 rounded-xl p-4 border border-slate-800 text-xs text-slate-400 space-y-1">
              <span className="text-emerald-400 font-semibold block">💡 تحليل تقني وتطبيقي:</span>
              <p>في التطبيق الحقيقي، يقوم الذكاء الاصطناعي بمطابقة الكلمات (Speech-to-Text) ثم قياس الإشارات الصوتية (Pitch, Duration, Formants) للتحقق من المدود والغنن ومخارج الحروف بدقة.</p>
            </div>
          </div>

          {/* AI Result View */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                  <Brain className="w-5 h-5 text-emerald-400" />
                  تقرير التقييم الصوتي والتجويدي
                </h3>
                {recordingResult && (
                  <span className="text-xs bg-emerald-500/20 text-emerald-300 px-2 py-0.5 rounded-full border border-emerald-500/30">
                    تم التحليل اللحظي
                  </span>
                )}
              </div>

              {recordingResult ? (
                <div className="space-y-4">
                  <div className="p-4 bg-slate-950 rounded-xl border border-emerald-500/30">
                    <span className="text-xs text-emerald-400 font-semibold block mb-1">الآية المسموعة والمطابقة:</span>
                    <p className="text-lg text-emerald-200 font-serif leading-loose text-center">
                      "{recordingResult.text}"
                    </p>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div className="bg-slate-950 p-3 rounded-lg border border-slate-800 text-center">
                      <span className="text-[11px] text-slate-400 block">دقة الحفظ والكلمات</span>
                      <span className="text-lg font-black text-emerald-400 font-mono">100%</span>
                    </div>
                    <div className="bg-slate-950 p-3 rounded-lg border border-slate-800 text-center">
                      <span className="text-[11px] text-slate-400 block">دقة الأحكام والتجويد</span>
                      <span className="text-lg font-black text-amber-400 font-mono">{recordingResult.accuracy}%</span>
                    </div>
                  </div>

                  {/* Rules Breakdown */}
                  <div className="space-y-2">
                    <span className="text-xs text-slate-300 font-semibold block">تفصيل أحكام المقطع:</span>
                    {recordingResult.details.map((item, idx) => (
                      <div key={idx} className="flex items-center justify-between bg-slate-950 px-3 py-2 rounded-lg border border-slate-800 text-xs">
                        <span className="font-bold text-slate-200">{item.rule} ({item.position})</span>
                        <span className="text-amber-300 text-[11px]">{item.status}</span>
                      </div>
                    ))}
                  </div>

                  <div className="p-4 bg-amber-950/30 border border-amber-500/30 rounded-xl text-xs space-y-1.5">
                    <div className="flex items-center gap-2 text-amber-400 font-semibold">
                      <AlertCircle className="w-4 h-4" />
                      توجيه المعلم التجويدي الذكي:
                    </div>
                    <p className="text-amber-200/90 leading-relaxed">
                      {recordingResult.tajweedNote}
                    </p>
                  </div>
                </div>
              ) : (
                <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-xl p-6 text-center text-slate-500">
                  <Volume2 className="w-12 h-12 mb-3 opacity-30" />
                  <p className="text-sm">لم يتم تسجيل تسميع بعد.</p>
                  <p className="text-xs text-slate-600 mt-1">اضغط على الميكروفون لبدء محاكاة التسميع الصوتي وتحليل الأحكام.</p>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-end gap-2">
              <button
                onClick={handleStartRecording}
                className="px-4 py-2 text-xs bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg transition-colors border border-slate-700 flex items-center gap-1.5 cursor-pointer"
              >
                <RefreshCw className="w-3.5 h-3.5" />
                إعادة التسميع
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: SPACED REPETITION */}
      {activeTab === 'spaced' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <BookOpen className="w-5 h-5 text-emerald-400" />
                خطة المراجعة بنظام التكرار المتباعد (Spaced Repetition Algorithm)
              </h3>
              <p className="text-slate-400 text-xs">
                يقيس التطبيق منحنى النسيان (Forgetting Curve) ويحدد موعد استدعاء كل سورة قبل أن يتلاشى الحفظ من الذاكرة قصيرة المدى.
              </p>
            </div>

            <div className="text-xs bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800 text-emerald-400 font-mono">
              محرك خوارزمية: SuperMemo SM-2 معدّل
            </div>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
            {flashcards.map((card) => (
              <div key={card.id} className="bg-slate-950 border border-slate-800 rounded-xl p-5 hover:border-emerald-500/40 transition-all shadow-md relative overflow-hidden group flex flex-col justify-between">
                <div>
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <h4 className="font-bold text-white text-base">{card.surah}</h4>
                      <span className="text-xs text-slate-400">الآيات: {card.ayats}</span>
                    </div>
                    <span className={`text-[11px] px-2.5 py-1 rounded-full font-bold ${
                      card.status === 'warning' ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30' :
                      card.status === 'info' ? 'bg-blue-500/20 text-blue-300 border border-blue-500/30' :
                      'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                    }`}>
                      {card.interval}
                    </span>
                  </div>

                  <div className="space-y-2 mt-4">
                    <div className="flex justify-between text-xs text-slate-400">
                      <span>ثبات الحفظ:</span>
                      <span className="text-slate-200 font-semibold">{card.progress}%</span>
                    </div>
                    <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden">
                      <div
                        className={`h-full transition-all duration-500 ${
                          card.status === 'warning' ? 'bg-amber-500' :
                          card.status === 'info' ? 'bg-blue-500' : 'bg-emerald-500'
                        }`}
                        style={{ width: `${card.progress}%` }}
                      ></div>
                    </div>
                    <div className="text-[10px] text-slate-500 text-left">
                      عدد مرات الاستدعاء: {card.repetitions}
                    </div>
                  </div>
                </div>

                <div className="mt-5 pt-3 border-t border-slate-800/80 flex items-center justify-between text-xs">
                  <span className="text-slate-400 text-[11px]">{card.level}</span>
                  <button
                    onClick={() => setActiveReviewCard(card)}
                    className="text-emerald-400 hover:text-emerald-300 font-semibold flex items-center gap-1 cursor-pointer"
                  >
                    مراجعة وتقييم
                    <ChevronLeft className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Interactive Modal / Review Feedback Panel */}
          {activeReviewCard && (
            <div className="p-5 bg-slate-950 border border-emerald-500/40 rounded-2xl space-y-4 animate-fade-in shadow-xl">
              <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                <span className="text-sm font-bold text-white flex items-center gap-2">
                  <Sparkles className="w-4 h-4 text-emerald-400" />
                  تقييم استدعاء الحفظ لـ ({activeReviewCard.surah} - {activeReviewCard.ayats})
                </span>
                <button onClick={() => setActiveReviewCard(null)} className="text-slate-500 hover:text-slate-300 text-xs">
                  إلغاء
                </button>
              </div>

              <p className="text-xs text-slate-300 leading-relaxed">
                كيف كان استحضارك للآيات عند المراجعة؟ سيقوم نظام التكرار المتباعد بإعادة جدولة وقت الاستدعاء التالي بناءً على اختيارك:
              </p>

              <div className="grid md:grid-cols-3 gap-3">
                <button
                  onClick={() => handleReviewFeedback(activeReviewCard.id, 'hard')}
                  className="p-3 bg-red-950/40 hover:bg-red-900/60 border border-red-500/40 rounded-xl text-center cursor-pointer transition-all space-y-1"
                >
                  <span className="font-bold text-red-300 block text-xs">صعب / ترددت كثيراً</span>
                  <span className="text-[10px] text-slate-400">إعادة التذكير خلال 4 ساعات</span>
                </button>

                <button
                  onClick={() => handleReviewFeedback(activeReviewCard.id, 'good')}
                  className="p-3 bg-blue-950/40 hover:bg-blue-900/60 border border-blue-500/40 rounded-xl text-center cursor-pointer transition-all space-y-1"
                >
                  <span className="font-bold text-blue-300 block text-xs">جيد / استحضار عادي</span>
                  <span className="text-[10px] text-slate-400">إعادة التذكير بعد 3 أيام</span>
                </button>

                <button
                  onClick={() => handleReviewFeedback(activeReviewCard.id, 'easy')}
                  className="p-3 bg-emerald-950/40 hover:bg-emerald-900/60 border border-emerald-500/40 rounded-xl text-center cursor-pointer transition-all space-y-1"
                >
                  <span className="font-bold text-emerald-300 block text-xs">ممتاز / حفظ راسخ بدون خطأ</span>
                  <span className="text-[10px] text-slate-400">إعادة التذكير بعد أسبوع</span>
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 3: MIND MAPS */}
      {activeTab === 'mindmap' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Layers className="w-5 h-5 text-emerald-400" />
                الربط البصري والذهني للآيات (الخرائط الموضوعية للقرآن)
              </h3>
              <p className="text-slate-400 text-sm">
                تقسيم السور إلى مواضيع وقصص مترابطة بصرياً لمساعدة الحفاظ على تذكر التسلسل وعدم الخلط بين المتشابهات.
              </p>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400">اختر السورة:</span>
              <select
                value={selectedSurah}
                onChange={(e) => setSelectedSurah(e.target.value)}
                className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-lg px-3 py-2 focus:outline-none focus:border-emerald-500"
              >
                <option value="الملك">سورة الملك</option>
                <option value="الكهف">سورة الكهف</option>
              </select>
            </div>
          </div>

          <div className="bg-slate-950/80 rounded-2xl p-6 border border-slate-800 space-y-6">
            <div className="text-center border-b border-slate-800 pb-4">
              <h4 className="text-base font-bold text-emerald-300 mb-1">
                {surahMindMaps[selectedSurah].title}
              </h4>
              <p className="text-xs text-slate-400">
                {surahMindMaps[selectedSurah].description}
              </p>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              {surahMindMaps[selectedSurah].topics.map((topic, idx) => (
                <div key={idx} className={`p-4 rounded-xl bg-gradient-to-br ${topic.bg} border ${topic.border} space-y-2.5 shadow-md relative group hover:scale-[1.01] transition-all`}>
                  <div className="flex justify-between items-center">
                    <span className="text-xs font-bold text-slate-300 bg-slate-950/60 px-2.5 py-0.5 rounded-full border border-slate-700">
                      المحور {idx + 1}
                    </span>
                    <span className="text-xs text-emerald-300 font-bold">{topic.range}</span>
                  </div>
                  <h5 className="font-bold text-white text-sm">{topic.title}</h5>
                  <div className="text-[11px] text-slate-300/80 bg-slate-950/40 p-2 rounded-lg border border-slate-800/80">
                    <span className="text-emerald-400 font-semibold">الكلمات والمفاهيم الرابطة: </span>
                    {topic.keywords}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
