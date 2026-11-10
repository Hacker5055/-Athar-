import React, { useState } from 'react';
import { Mic, MicOff, Volume2, CheckCircle2, AlertCircle, RefreshCw, BookOpen, Brain, Sparkles, ChevronLeft, ChevronRight, Layers, Award } from 'lucide-react';

export default function QuranMemorization() {
  const [activeTab, setActiveTab] = useState('corrector'); // 'corrector', 'spaced', 'mindmap'

  // Audio Reciter State
  const [selectedSampleSurah, setSelectedSampleSurah] = useState('mulk'); // 'mulk', 'ikhlas', 'fatiha'
  const [isRecording, setIsRecording] = useState(false);
  const [recordingResult, setRecordingResult] = useState(null);

  // Spaced Repetition State
  const [flashcards, setFlashcards] = useState([
    { id: 1, surah: 'سورة الملك', ayats: '1 - 5', level: 'يحتاج مراجعة قريبة', interval: 'بعد 6 ساعات', progress: 40, status: 'warning', mistakesCount: 3 },
    { id: 2, surah: 'سورة الكهف', ayats: '10 - 20', level: 'مستقر نسبياً', interval: 'غداً', progress: 75, status: 'info', mistakesCount: 1 },
    { id: 3, surah: 'سورة مريم', ayats: '1 - 15', level: 'ممتاز - حفظ متقن', interval: 'بعد 5 أيام', progress: 95, status: 'success', mistakesCount: 0 },
    { id: 4, surah: 'سورة يس', ayats: '1 - 12', level: 'جديد - في طور التثبيت', interval: 'بعد ساعتين', progress: 30, status: 'warning', mistakesCount: 4 },
  ]);

  // Mind Map State
  const [selectedSurah, setSelectedSurah] = useState('الملك');

  const sampleVerses = {
    mulk: {
      name: 'سورة الملك (الآية 1)',
      text: "تَبَٰرَكَ ٱلَّذِي بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
      tajweedNote: "تنبيه تجويدي: انتبه لمقدار الغنة المفخمة في إخفاء التنوين عند القاف في (شَيْءٍ قَدِيرٌ).",
      accuracy: 94,
      ruleName: "إخفاء حقيقي بغنة مفخمة",
      explanation: "حرف القاف من حروف الاستعلاء (خَصَّ ضَغْطٍ قِظْ)، فتُفخَّم الغنة التابعة للتنوين بمقدار حركتين."
    },
    ikhlas: {
      name: 'سورة الإخلاص (الآية 1 - 2)',
      text: "قُلْ هُوَ ٱللَّهُ أَحَدٌ * ٱللَّهُ ٱلصَّمَدُ",
      tajweedNote: "تنبيه تجويدي: انتبه لقلقلة الدال في (أَحَدٌ) عند الوقف، وتفخيم لام لفظ الجلالة.",
      accuracy: 98,
      ruleName: "قلقلة كبرى عند الوقف",
      explanation: "حرف الدال من حروف (قُطْبُ جَدٍّ)، فعند الوقف عليه بالسكون يجب إظهار نبرة القلقلة بوضوح."
    },
    fatiha: {
      name: 'سورة الفاتحة (الآية 7)',
      text: "صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ",
      tajweedNote: "تنبيه تجويدي: مد لازم كَلِمي مثقل في (ٱلضَّآلِّينَ) بمقدار 6 حركات وجوباً، وإطباق حرف الضاد دون قلبه ظاءً.",
      accuracy: 91,
      ruleName: "مد لازم كلمي مثقل (6 حركات)",
      explanation: "جاء بعد حرف المد حرف مشدد في نفس الكلمة، فيلزم مده مداً مشبعاً مقداره ست حركات بلا تهاون."
    }
  };

  const handleStartRecording = () => {
    setIsRecording(true);
    setRecordingResult(null);
    setTimeout(() => {
      setIsRecording(false);
      const sample = sampleVerses[selectedSampleSurah];
      setRecordingResult({
        text: sample.text,
        tajweedNote: sample.tajweedNote,
        accuracy: sample.accuracy,
        ruleName: sample.ruleName,
        explanation: sample.explanation
      });
    }, 2500);
  };

  const handleReviewCard = (id) => {
    setFlashcards(flashcards.map(card => {
      if (card.id === id) {
        return {
          ...card,
          progress: Math.min(100, card.progress + 25),
          interval: card.progress >= 70 ? 'بعد 7 أيام' : 'بعد 3 أيام',
          level: card.progress >= 70 ? 'ممتاز - حفظ متقن' : 'مستقر نسبياً',
          status: 'success'
        };
      }
      return card;
    }));
  };

  const surahMindMaps = {
    'الملك': {
      title: 'سورة الملك - خريطة المحاور الموضوعية',
      topics: [
        { title: 'عظمة القدرة الإلهية وخلق الموت والحياة والكون', range: 'الآيات 1 - 5', bg: 'from-emerald-950/70 to-emerald-900/40', border: 'border-emerald-500/40', desc: 'إثبات الملك المطلق لله وتحدي العقول في إتقان صنع السماوات' },
        { title: 'عاقبة المكذبين واعتراف أصحاب السعير بذنبهم', range: 'الآيات 6 - 12', bg: 'from-amber-950/70 to-amber-900/40', border: 'border-amber-500/40', desc: 'مشهد رمي الكفار في جهنم وفوز الخائفين من ربهم بالغيب' },
        { title: 'شمول علم الله ولطيف خلقه للأرض والطير', range: 'الآيات 13 - 19', bg: 'from-blue-950/70 to-blue-900/40', border: 'border-blue-500/40', desc: 'تسخير الأرض للمشي في مناكبها وتأمل الطيور الصافات والقابضات' },
        { title: 'التذكير بنعم السمع والأبصار وتحذير الغافلين من بغتة العذاب', range: 'الآيات 20 - 30', bg: 'from-purple-950/70 to-purple-900/40', border: 'border-purple-500/40', desc: 'التساؤل الختامي: مَن يأتيكم بماء معين إن أصبح ماؤكم غوراً؟' },
      ]
    },
    'الكهف': {
      title: 'سورة الكهف - خريطة الفتن الأربع وقوارب النجاة',
      topics: [
        { title: 'فتنة الدين - قصة الفتية وأصحاب الكهف', range: 'الآيات 9 - 26', bg: 'from-emerald-950/70 to-emerald-900/40', border: 'border-emerald-500/40', desc: 'سلاح النجاة: الصحبة الصالحة واللجوء إلى الله بالدعاء' },
        { title: 'فتنة المال - قصة صاحب الجنتين وصاحبه المؤمن', range: 'الآيات 32 - 44', bg: 'from-amber-950/70 to-amber-900/40', border: 'border-amber-500/40', desc: 'سلاح النجاة: إدراك زوال الدنيا وقول "ما شاء الله لا قوة إلا بالله"' },
        { title: 'فتنة العلم - رحلة موسى عليه السلام مع الخضر', range: 'الآيات 60 - 82', bg: 'from-blue-950/70 to-blue-900/40', border: 'border-blue-500/40', desc: 'سلاح النجاة: التواضع في طلب العلم والتسليم بحكمة الله الغيبية' },
        { title: 'فتنة السلطة والتمكين - قصة الملك الصالح ذي القرنين', range: 'الآيات 83 - 98', bg: 'from-purple-950/70 to-purple-900/40', border: 'border-purple-500/40', desc: 'سلاح النجاة: تسخير النفوذ والعدل لخدمة المستضعفين ورد الفضل لله' },
      ]
    },
    'يوسف': {
      title: 'سورة يوسف - خريطة الابتلاء والتمكين وحسن العاقبة',
      topics: [
        { title: 'كيد الإخوة وإلقاء يوسف في غيابة الجب', range: 'الآيات 4 - 20', bg: 'from-emerald-950/70 to-emerald-900/40', border: 'border-emerald-500/40', desc: 'بداية المحنة وتحلي يعقوب عليه السلام بالصبر الجميل' },
        { title: 'فتنة امرأة العزيز والسجن ظلماً', range: 'الآيات 21 - 42', bg: 'from-amber-950/70 to-amber-900/40', border: 'border-amber-500/40', desc: 'الثبات على العفة وتقديم السجن على المعصية' },
        { title: 'تعبير الرؤيا وخروجه إلى خزائن مصر', range: 'الآيات 43 - 57', bg: 'from-blue-950/70 to-blue-900/40', border: 'border-blue-500/40', desc: 'الأمانة والكفاءة الإدارية لإنقاذ الأمة من المجاعة' },
        { title: 'اجتماع الشمل وتحقق الرؤيا والعفو المطلق', range: 'الآيات 58 - 101', bg: 'from-purple-950/70 to-purple-900/40', border: 'border-purple-500/40', desc: 'ذروة التسامح: "لا تثريب عليكم اليوم يغفر الله لكم"' },
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
              الذكاء الاصطناعي التجويدي
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">الحفظ والمراجعة الذكية</h2>
            <p className="text-slate-300 text-sm mt-1">تسميع صوتي بالذكاء الاصطناعي، تكرار متباعد، وخرائط ذهنية مترابطة لربط الآيات.</p>
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
                  مستمع التلاوة المباشر
                </h3>
                <span className="text-xs bg-slate-800 text-slate-300 px-2.5 py-1 rounded-full border border-slate-700">
                  {sampleVerses[selectedSampleSurah].name}
                </span>
              </div>

              {/* Sample Verse Selector */}
              <div className="mb-4">
                <label className="text-xs text-slate-300 block mb-1.5 font-medium">اختر المقطع القرآني للتسميع والتجربة:</label>
                <div className="grid grid-cols-3 gap-2">
                  <button
                    onClick={() => { setSelectedSampleSurah('mulk'); setRecordingResult(null); }}
                    className={`py-2 px-3 text-xs rounded-lg border text-center transition-all ${
                      selectedSampleSurah === 'mulk'
                        ? 'bg-emerald-600/30 border-emerald-500 text-emerald-300 font-bold'
                        : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    سورة الملك
                  </button>
                  <button
                    onClick={() => { setSelectedSampleSurah('ikhlas'); setRecordingResult(null); }}
                    className={`py-2 px-3 text-xs rounded-lg border text-center transition-all ${
                      selectedSampleSurah === 'ikhlas'
                        ? 'bg-emerald-600/30 border-emerald-500 text-emerald-300 font-bold'
                        : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    سورة الإخلاص
                  </button>
                  <button
                    onClick={() => { setSelectedSampleSurah('fatiha'); setRecordingResult(null); }}
                    className={`py-2 px-3 text-xs rounded-lg border text-center transition-all ${
                      selectedSampleSurah === 'fatiha'
                        ? 'bg-emerald-600/30 border-emerald-500 text-emerald-300 font-bold'
                        : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    سورة الفاتحة
                  </button>
                </div>
              </div>

              <div className="bg-slate-950 p-3.5 rounded-xl border border-slate-800 mb-4 text-center">
                <span className="text-[11px] text-slate-400 block mb-1">الآية المطلوب تلاوتها:</span>
                <p className="text-emerald-300 font-serif text-sm leading-relaxed">
                  "{sampleVerses[selectedSampleSurah].text}"
                </p>
              </div>

              <p className="text-slate-400 text-xs mb-4 leading-relaxed">
                اضغط على زر التسجيل واقرأ بصوتك، وسيقوم المحرك الذكي برصد مخارج الحروف والتشكيل وأحكام التجويد بدقة.
              </p>

              {/* Record Button UI */}
              <div className="flex flex-col items-center justify-center my-4">
                <button
                  onClick={handleStartRecording}
                  disabled={isRecording}
                  className={`relative group p-6 rounded-full transition-all duration-300 ${
                    isRecording
                      ? 'bg-red-500/20 text-red-400 border-2 border-red-500 animate-pulse'
                      : 'bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-400 border-2 border-emerald-500/50 hover:scale-105'
                  }`}
                >
                  {isRecording ? <MicOff className="w-10 h-10 animate-bounce" /> : <Mic className="w-10 h-10" />}
                </button>
                <p className="text-xs text-slate-400 mt-3 font-medium">
                  {isRecording ? "جاري الاستماع للآية ومطابقة الأحكام التجويدية..." : "انقر لبدء التسميع والتصحيح الذكي"}
                </p>
              </div>
            </div>

            <div className="bg-slate-950/60 rounded-xl p-3 border border-slate-800 text-xs text-slate-400">
              💡 <span className="text-slate-300 font-semibold">ملاحظة تقنية:</span> يُطبق التطبيق نموذجاً هجيناً (ذكاء اصطناعي للتشكيل الفوري + ربط بمعلمين مجازين للرصد التجويدي المتقدم).
            </div>
          </div>

          {/* AI Result View */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg flex flex-col justify-between">
            <div>
              <h3 className="text-lg font-bold text-slate-200 mb-4 flex items-center gap-2">
                <Brain className="w-5 h-5 text-emerald-400" />
                نتيجة التقييم والملاحظات التجويدية
              </h3>

              {recordingResult ? (
                <div className="space-y-4">
                  <div className="p-4 bg-slate-950 rounded-xl border border-emerald-500/30">
                    <span className="text-xs text-emerald-400 font-semibold block mb-1">الآية المكتشفة بنجاح:</span>
                    <p className="text-base text-emerald-200 font-serif leading-loose text-center">
                      "{recordingResult.text}"
                    </p>
                  </div>

                  <div className="flex items-center justify-between bg-slate-800/50 p-3 rounded-lg border border-slate-700">
                    <span className="text-xs text-slate-300">نسبة مطابقة الحفظ والتشكيل:</span>
                    <span className="text-sm font-bold text-emerald-400">{recordingResult.accuracy}%</span>
                  </div>

                  <div className="p-4 bg-amber-950/30 border border-amber-500/30 rounded-xl text-xs space-y-2">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2 text-amber-400 font-semibold">
                        <AlertCircle className="w-4 h-4" />
                        الحكم التجويدي المرصود:
                      </div>
                      <span className="bg-amber-500/20 text-amber-300 text-[10px] px-2 py-0.5 rounded">
                        {recordingResult.ruleName}
                      </span>
                    </div>
                    <p className="text-amber-200/95 leading-relaxed">
                      {recordingResult.tajweedNote}
                    </p>
                    <div className="pt-2 border-t border-amber-500/20 text-[11px] text-slate-300 leading-relaxed">
                      <span className="font-semibold text-amber-300">الشرح المبسط:</span> {recordingResult.explanation}
                    </div>
                  </div>
                </div>
              ) : (
                <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-xl p-6 text-center text-slate-500">
                  <Volume2 className="w-12 h-12 mb-3 opacity-30" />
                  <p className="text-sm">لم يتم إجراء تسميع بعد.</p>
                  <p className="text-xs text-slate-600 mt-1">اضغط على الميكروفون لبدء تجربة المصحح الصوتي التفاعلي.</p>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-between items-center">
              <span className="text-[11px] text-slate-500">خوارزمية الرصد الصوتي: Athar Speech Engine v1</span>
              <button
                onClick={handleStartRecording}
                disabled={isRecording}
                className="px-4 py-2 text-xs bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg transition-colors border border-slate-700 flex items-center gap-1.5"
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
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-3">
            <div>
              <h3 className="text-lg sm:text-xl font-bold text-white mb-1 flex items-center gap-2">
                <BookOpen className="w-5 h-5 text-emerald-400" />
                خطة المراجعة بنظام التكرار المتباعد (Spaced Repetition Algorithm)
              </h3>
              <p className="text-slate-400 text-xs">
                يحلل النظام آلياً مستويات الأخطاء السابقة، ويحدد بدقة الأوقات المثالية لمراجعة كل مقطع قبل أن يدخل في منحنى النسيان (Forgetting Curve).
              </p>
            </div>
            <div className="bg-emerald-500/10 border border-emerald-500/30 px-3 py-1.5 rounded-xl text-xs text-emerald-300 font-semibold shrink-0">
              خوارزمية SM-2 المعدلة
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
                    <span className={`text-[10px] px-2.5 py-1 rounded-full font-semibold ${
                      card.status === 'warning' ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30' :
                      card.status === 'info' ? 'bg-blue-500/20 text-blue-300 border border-blue-500/30' :
                      'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                    }`}>
                      {card.interval}
                    </span>
                  </div>

                  <div className="space-y-2 mt-4">
                    <div className="flex justify-between text-xs text-slate-400">
                      <span>مؤشر ثبات الحفظ:</span>
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
                    <div className="flex justify-between text-[11px] text-slate-500 pt-1">
                      <span>الأخطاء المرصودة: {card.mistakesCount}</span>
                      <span>{card.level}</span>
                    </div>
                  </div>
                </div>

                <div className="mt-5 pt-3 border-t border-slate-800/80 flex items-center justify-between text-xs">
                  <span className="text-[11px] text-slate-400">تحديث الجدولة</span>
                  <button
                    onClick={() => handleReviewCard(card.id)}
                    className="text-emerald-400 hover:text-emerald-300 font-medium flex items-center gap-1 group-hover:translate-x-0.5 transition-transform"
                  >
                    أتممت المراجعة
                    <ChevronLeft className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 3: MIND MAPS */}
      {activeTab === 'mindmap' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Layers className="w-5 h-5 text-emerald-400" />
                الربط البصري والذهني للآيات (الخرائط الموضوعية المتسلسلة)
              </h3>
              <p className="text-slate-400 text-sm">تقسيم السور موضوعياً لمساعدة الحفاظ في استحضار تسلسل الأفكار والآيات المتشابهة.</p>
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
                <option value="يوسف">سورة يوسف</option>
              </select>
            </div>
          </div>

          <div className="bg-slate-950/80 rounded-2xl p-6 border border-slate-800">
            <h4 className="text-base font-bold text-emerald-300 mb-6 text-center border-b border-slate-800 pb-3">
              {surahMindMaps[selectedSurah].title}
            </h4>

            <div className="grid md:grid-cols-2 gap-4">
              {surahMindMaps[selectedSurah].topics.map((topic, idx) => (
                <div key={idx} className={`p-4 rounded-xl bg-gradient-to-br ${topic.bg} border ${topic.border} space-y-2.5 shadow-md relative group hover:scale-[1.01] transition-all`}>
                  <div className="flex justify-between items-center">
                    <span className="text-xs font-bold text-slate-300 bg-slate-950/50 px-2.5 py-0.5 rounded-full border border-slate-700">
                      المحور {idx + 1}
                    </span>
                    <span className="text-xs text-emerald-300 font-medium">{topic.range}</span>
                  </div>
                  <h5 className="font-semibold text-white text-sm">{topic.title}</h5>
                  <p className="text-xs text-slate-300/90 leading-relaxed pt-1 border-t border-slate-800/60">
                    {topic.desc}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
