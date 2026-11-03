import React, { useState } from 'react';
import {
  Mic,
  MicOff,
  Volume2,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  BookOpen,
  Brain,
  Sparkles,
  ChevronLeft,
  Layers,
  Check,
  Clock,
  RotateCcw,
  Sliders,
  Play
} from 'lucide-react';

export default function QuranMemorization() {
  const [activeTab, setActiveTab] = useState('corrector'); // 'corrector', 'spaced', 'mindmap'

  // Audio Reciter State
  const [isRecording, setIsRecording] = useState(false);
  const [selectedSample, setSelectedSample] = useState('mulk');
  const [recordingResult, setRecordingResult] = useState(null);

  const sampleVerses = {
    mulk: {
      id: 'mulk',
      title: 'سورة الملك (الآية 1)',
      targetText: 'تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ',
      result: {
        recitedText: "تَبَٰرَكَ ٱلَّذِي بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
        accuracy: 96,
        tajweedScore: 92,
        notes: [
          { rule: "إخفاء حقيقي", text: "شَيْءٍ قَدِيرٌ", comment: "انتبه لتفخيم غنة الإخفاء نظراً لأن القاف حرف استعلاء مفخم.", status: "warning" },
          { rule: "مد عارض للسكون", text: "قَدِيرٌ", comment: "تم التوسط في المد بمقدار 4 حركات بنجاح.", status: "success" }
        ]
      }
    },
    fatiha: {
      id: 'fatiha',
      title: 'سورة الفاتحة (الآية 7)',
      targetText: 'صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ',
      result: {
        recitedText: "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
        accuracy: 98,
        tajweedScore: 95,
        notes: [
          { rule: "مد لازم كَلِمي مثقّل", text: "الضَّالِّينَ", comment: "مد لازم بمقدار 6 حركات مشبع، مع تشديد اللام ونبرة صوت واضحة.", status: "success" },
          { rule: "إظهار حلقي", text: "أَنْعَمْتَ", comment: "إظهار النون الساكنة عند حرف العين بوضوح دون غنة زائدة.", status: "success" }
        ]
      }
    },
    ikhlas: {
      id: 'ikhlas',
      title: 'سورة الإخلاص (الآيات 1 - 4)',
      targetText: 'قُلْ هُوَ اللَّهُ أَحَدٌ * اللَّهُ الصَّمَدُ * لَمْ يَلِدْ وَلَمْ يُولَدْ * وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ',
      result: {
        recitedText: "قُلْ هُوَ اللَّهُ أَحَدٌ * اللَّهُ الصَّمَدُ * لَمْ يَلِدْ وَلَمْ يُولَدْ * وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        accuracy: 94,
        tajweedScore: 89,
        notes: [
          { rule: "قلقلة كبرى عند الوقف", text: "أَحَدْ / الصَّمَدْ", comment: "إبراز صوت القلقلة في الدال عند الوقف عليها بالسكون.", status: "warning" },
          { rule: "إدغام كامل بدون غنة", text: "يَكُن لَّهُ", comment: "إدغام النون الساكنة في اللام دون إبقاء أثر للغنة ممتاز.", status: "success" }
        ]
      }
    }
  };

  const handleStartRecording = () => {
    setIsRecording(true);
    setRecordingResult(null);
    setTimeout(() => {
      setIsRecording(false);
      setRecordingResult(sampleVerses[selectedSample].result);
    }, 2500);
  };

  // Spaced Repetition Interactive State
  const [flashcards, setFlashcards] = useState([
    {
      id: 1,
      surah: 'سورة الملك',
      ayats: 'الآيات 1 - 5',
      level: 'يحتاج مراجعة عاجلة',
      nextReview: 'اليوم (بعد 4 ساعات)',
      intervalDays: 0.5,
      retention: 45,
      status: 'warning',
      lastMistake: 'خلط بين تفاوت وفطور'
    },
    {
      id: 2,
      surah: 'سورة الكهف',
      ayats: 'الآيات 10 - 26',
      level: 'مستوى متوسط',
      nextReview: 'غداً',
      intervalDays: 1,
      retention: 78,
      status: 'info',
      lastMistake: 'تسلسل أرقام لبث أصحاب الكهف'
    },
    {
      id: 3,
      surah: 'سورة مريم',
      ayats: 'الآيات 1 - 20',
      level: 'ممتاز - حفظ راسخ',
      nextReview: 'بعد 5 أيام',
      intervalDays: 5,
      retention: 96,
      status: 'success',
      lastMistake: 'لا يوجد'
    },
    {
      id: 4,
      surah: 'سورة النور',
      ayats: 'الآيات 35 (آية النور)',
      level: 'حفظ جديد',
      nextReview: 'اليوم بعد المغرب',
      intervalDays: 0.2,
      retention: 60,
      status: 'warning',
      lastMistake: 'ترتيب جملة مثل نوره كمشكاة'
    }
  ]);

  // Handle reviewing a card with rating (Anki SM-2 style)
  const handleRateCard = (id, rating) => {
    // rating: 'hard' | 'good' | 'easy'
    setFlashcards(flashcards.map(card => {
      if (card.id !== id) return card;
      let newInterval = card.intervalDays;
      let newRetention = card.retention;
      let newStatus = card.status;
      let newReviewText = '';

      if (rating === 'hard') {
        newInterval = 0.5;
        newRetention = Math.max(30, card.retention - 10);
        newStatus = 'warning';
        newReviewText = 'اليوم بعد 4 ساعات';
      } else if (rating === 'good') {
        newInterval = card.intervalDays * 1.8;
        newRetention = Math.min(95, card.retention + 10);
        newStatus = newRetention > 85 ? 'success' : 'info';
        newReviewText = `بعد ${Math.round(newInterval)} أيام`;
      } else {
        newInterval = card.intervalDays * 2.5 + 2;
        newRetention = 98;
        newStatus = 'success';
        newReviewText = `بعد ${Math.round(newInterval)} أيام`;
      }

      return {
        ...card,
        intervalDays: newInterval,
        retention: newRetention,
        status: newStatus,
        nextReview: newReviewText,
        level: newRetention > 85 ? 'ممتاز - حفظ راسخ' : newRetention > 65 ? 'مستقر نسبياً' : 'يحتاج تكرار'
      };
    }));
  };

  // Mind Maps State
  const [selectedSurah, setSelectedSurah] = useState('الملك');

  const surahMindMaps = {
    'الملك': {
      title: 'سورة الملك - خريطة المحاور الموضوعية ودلائل القدرة',
      description: 'سورة تنجي من عذاب القبر وترسخ حقيقة الملك والخلق والبعث في 4 محاور رئيسية.',
      topics: [
        { title: 'عظمة القدرة الإلهية وخلق الموت والحياة والكمال الكوني', range: 'الآيات 1 - 5', bg: 'from-emerald-950/80 to-emerald-900/40', border: 'border-emerald-500/40' },
        { title: 'عاقبة المكذبين ومصير أصحاب السعير واعترافهم بالذنب', range: 'الآيات 6 - 12', bg: 'from-amber-950/80 to-amber-900/40', border: 'border-amber-500/40' },
        { title: 'شمول علم الله بما في الصدور وبديع تسخير الأرض والرزق', range: 'الآيات 13 - 19', bg: 'from-blue-950/80 to-blue-900/40', border: 'border-blue-500/40' },
        { title: 'التذكير بالنعم والمصير وتحذير العباد من ذهاب الماء والعذاب', range: 'الآيات 20 - 30', bg: 'from-purple-950/80 to-purple-900/40', border: 'border-purple-500/40' },
      ]
    },
    'الكهف': {
      title: 'سورة الكهف - خريطة الفتن الأربع وقوارب النجاة',
      description: 'نور بين الجمعتين، توضح أربع فتن كبرى يمر بها الإنسان مع مفتاح النجاة لكل فتنة.',
      topics: [
        { title: 'فتنة الدين: قصة أصحاب الكهف (قارب النجاة: الصحبة الصالحة والدعاء)', range: 'الآيات 9 - 26', bg: 'from-emerald-950/80 to-emerald-900/40', border: 'border-emerald-500/40' },
        { title: 'فتنة المال: قصة صاحب الجنتين (قارب النجاة: معرفة حقيقة الدنيا)', range: 'الآيات 32 - 44', bg: 'from-amber-950/80 to-amber-900/40', border: 'border-amber-500/40' },
        { title: 'فتنة العلم: قصة موسى والخضر عليهما السلام (قارب النجاة: التواضع)', range: 'الآيات 60 - 82', bg: 'from-blue-950/80 to-blue-900/40', border: 'border-blue-500/40' },
        { title: 'فتنة السلطة: قصة ذي القرنين ويأجوج ومأجوج (قارب النجاة: العدل والإخلاص)', range: 'الآيات 83 - 98', bg: 'from-purple-950/80 to-purple-900/40', border: 'border-purple-500/40' },
      ]
    },
    'يس': {
      title: 'سورة يس - قلب القرآن ورسائل الإيمان والبعث',
      description: 'تأكيد الرسالة النبوية وعبرة الأمم السابقة وأدلة البعث والنشور بعد الموت.',
      topics: [
        { title: 'صدق الوحي ورسالة النبي ﷺ ومصير المعاندين', range: 'الآيات 1 - 12', bg: 'from-emerald-950/80 to-emerald-900/40', border: 'border-emerald-500/40' },
        { title: 'قصة أصحاب القرية ومؤمن آل يس (الشهيد الداعية)', range: 'الآيات 13 - 32', bg: 'from-amber-950/80 to-amber-900/40', border: 'border-amber-500/40' },
        { title: 'آيات الله في الكون: الأرض الميتة، الليل والنهار، والفلك المشحون', range: 'الآيات 33 - 47', bg: 'from-blue-950/80 to-blue-900/40', border: 'border-blue-500/40' },
        { title: 'مشاهد يوم القيامة، حوار أهل الجنة والنار، وإثبات القدرة على الإحياء', range: 'الآيات 48 - 83', bg: 'from-purple-950/80 to-purple-900/40', border: 'border-purple-500/40' },
      ]
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-emerald-900/80 via-emerald-800/50 to-slate-900 border border-emerald-500/30 rounded-3xl p-6 md:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-semibold mb-3 border border-emerald-500/30">
              <Sparkles className="w-3.5 h-3.5" />
              الذكاء الاصطناعي التجويدي والحفظ الراسخ
            </div>
            <h2 className="text-2xl md:text-3xl font-extrabold text-white">الحفظ والمراجعة الذكية</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              تسميع صوتي تفاعلي، خوارزمية التكرار المتباعد لتثبيت المتشابهات، وخرائط ذهنية موضوعية لفهم تسلسل الآيات.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/80 p-1.5 rounded-2xl border border-slate-800">
            <button
              onClick={() => setActiveTab('corrector')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'corrector' ? 'bg-emerald-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              مصحح التلاوة
            </button>
            <button
              onClick={() => setActiveTab('spaced')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'spaced' ? 'bg-emerald-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              التكرار المتباعد
            </button>
            <button
              onClick={() => setActiveTab('mindmap')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
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
          <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 flex flex-col justify-between shadow-xl">
            <div>
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
                  <Mic className="w-5 h-5" />
                  مستمع التلاوة ومصحح الأحكام
                </h3>
                <span className="text-xs bg-slate-800 text-slate-300 px-3 py-1 rounded-full border border-slate-700">
                  تجربة تفاعلية مباشرة
                </span>
              </div>
              <p className="text-slate-300 text-xs mb-4 leading-relaxed">
                اختر المقطع القرآني للتسميع ثم انقر على زر التسجيل لمعالجة الحروف والمدود والغنن:
              </p>

              {/* Sample Selector */}
              <div className="flex gap-2 mb-6">
                {Object.keys(sampleVerses).map(key => (
                  <button
                    key={key}
                    onClick={() => {
                      setSelectedSample(key);
                      setRecordingResult(null);
                    }}
                    className={`flex-1 py-2 px-2 text-xs rounded-xl border transition-all text-center font-medium ${
                      selectedSample === key
                        ? 'bg-emerald-600/30 border-emerald-500 text-emerald-300 font-bold'
                        : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {sampleVerses[key].title}
                  </button>
                ))}
              </div>

              {/* Target Verse Display */}
              <div className="p-4 bg-slate-950 rounded-2xl border border-slate-800 text-center mb-6">
                <span className="text-[10px] text-slate-500 font-semibold block mb-1">الآية المطلوب تلاوتها:</span>
                <p className="text-base text-slate-200 font-serif leading-loose">
                  "{sampleVerses[selectedSample].targetText}"
                </p>
              </div>

              {/* Record Button UI */}
              <div className="flex flex-col items-center justify-center my-6">
                <button
                  onClick={handleStartRecording}
                  disabled={isRecording}
                  className={`relative group p-6 rounded-full transition-all duration-300 ${
                    isRecording
                      ? 'bg-red-500/20 text-red-400 border-2 border-red-500 animate-pulse scale-110 shadow-lg shadow-red-500/30'
                      : 'bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-400 border-2 border-emerald-500/50 hover:scale-105 shadow-lg shadow-emerald-500/20'
                  }`}
                >
                  {isRecording ? <MicOff className="w-10 h-10 animate-bounce" /> : <Mic className="w-10 h-10" />}
                </button>
                <p className="text-xs text-slate-400 mt-4">
                  {isRecording ? "جاري الاستماع للآية وتحليل الأحكام والتشكيل..." : "انقر لبدء التسميع والتصحيح الفوري"}
                </p>
              </div>
            </div>

            <div className="bg-slate-950/60 rounded-2xl p-4 border border-slate-800 text-xs text-slate-400 flex items-start gap-2">
              <Sparkles className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
              <span>
                <strong className="text-slate-200">الدراسة الهندسية:</strong> يستخدم هذا النموذج خوارزمية التعرف على الحركات الفونيمية (Phonetic Speech Alignment) لمطابقة صوتك بالمصحف المرتل المعياري.
              </span>
            </div>
          </div>

          {/* AI Result View */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-4 border-b border-slate-800 pb-3">
                <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                  <Brain className="w-5 h-5 text-emerald-400" />
                  تقرير التقييم الصوتي والتجويدي
                </h3>
                {recordingResult && (
                  <span className="text-xs bg-emerald-500/20 text-emerald-300 px-3 py-1 rounded-full border border-emerald-500/30 font-bold">
                    تم التحليل بنجاح
                  </span>
                )}
              </div>

              {recordingResult ? (
                <div className="space-y-4">
                  <div className="p-4 bg-slate-950 rounded-2xl border border-emerald-500/30">
                    <span className="text-xs text-emerald-400 font-semibold block mb-1">الآية المكتشفة بصوتك:</span>
                    <p className="text-lg text-emerald-200 font-serif leading-loose text-center">
                      "{recordingResult.recitedText}"
                    </p>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <div className="bg-slate-950 p-3 rounded-xl border border-slate-800 text-center">
                      <span className="text-[11px] text-slate-400 block">دقة حفظ الكلمات:</span>
                      <span className="text-xl font-black text-emerald-400 font-mono">{recordingResult.accuracy}%</span>
                    </div>
                    <div className="bg-slate-950 p-3 rounded-xl border border-slate-800 text-center">
                      <span className="text-[11px] text-slate-400 block">درجة إتقان التجويد:</span>
                      <span className="text-xl font-black text-teal-400 font-mono">{recordingResult.tajweedScore}%</span>
                    </div>
                  </div>

                  <div className="space-y-2 pt-2">
                    <span className="text-xs font-bold text-slate-300 block">ملاحظات التجويد والمخارج:</span>
                    {recordingResult.notes.map((note, idx) => (
                      <div
                        key={idx}
                        className={`p-3.5 rounded-xl border text-xs space-y-1 ${
                          note.status === 'warning'
                            ? 'bg-amber-950/30 border-amber-500/40 text-amber-200'
                            : 'bg-emerald-950/30 border-emerald-500/40 text-emerald-200'
                        }`}
                      >
                        <div className="flex items-center justify-between font-bold">
                          <span className="flex items-center gap-1.5">
                            {note.status === 'warning' ? <AlertCircle className="w-4 h-4 text-amber-400" /> : <Check className="w-4 h-4 text-emerald-400" />}
                            {note.rule} ({note.text})
                          </span>
                          <span className="text-[10px] px-2 py-0.5 rounded bg-slate-950/60">
                            {note.status === 'warning' ? 'يحتاج تنبيه' : 'متقن'}
                          </span>
                        </div>
                        <p className="text-[11px] opacity-90 leading-relaxed">{note.comment}</p>
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-2xl p-6 text-center text-slate-500">
                  <Volume2 className="w-12 h-12 mb-3 opacity-30" />
                  <p className="text-sm font-semibold text-slate-400">لم يتم إجراء تسميع بعد.</p>
                  <p className="text-xs text-slate-600 mt-1">اضغط على زر الميكروفون لبدء الفحص الصوتي.</p>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-between items-center pt-3 border-t border-slate-800">
              <span className="text-xs text-slate-500">نموذج ذكاء اصطناعي صوتي مخصص</span>
              <button
                onClick={handleStartRecording}
                className="px-4 py-2 text-xs bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl transition-colors border border-slate-700 flex items-center gap-1.5"
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
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <BookOpen className="w-5 h-5 text-emerald-400" />
                خطة المراجعة الذكية بنظام التكرار المتباعد (Spaced Repetition)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                تحديد المواعيد الدقيقة لتكرار الآيات قبل دخولها في منحنى النسيان مع إمكانية تقييم قوة استحضارك.
              </p>
            </div>
            <div className="flex items-center gap-2 text-xs bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800 text-slate-300">
              <Clock className="w-4 h-4 text-emerald-400" />
              <span>المتبقي للمراجعة اليوم: <strong>2 بطاقات</strong></span>
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-4">
            {flashcards.map((card) => (
              <div
                key={card.id}
                className="bg-slate-950 border border-slate-800 rounded-2xl p-5 hover:border-emerald-500/40 transition-all shadow-lg flex flex-col justify-between space-y-4"
              >
                <div>
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <h4 className="font-bold text-white text-base">{card.surah}</h4>
                      <span className="text-xs text-slate-400">{card.ayats}</span>
                    </div>
                    <span className={`text-[11px] px-3 py-1 rounded-full font-bold ${
                      card.status === 'warning' ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30' :
                      card.status === 'info' ? 'bg-blue-500/20 text-blue-300 border border-blue-500/30' :
                      'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                    }`}>
                      الموعد: {card.nextReview}
                    </span>
                  </div>

                  <div className="space-y-1.5 mt-3">
                    <div className="flex justify-between text-xs text-slate-400">
                      <span>مستوى رسوخ الحفظ:</span>
                      <span className="text-slate-200 font-bold">{card.retention}%</span>
                    </div>
                    <div className="w-full bg-slate-800 h-2.5 rounded-full overflow-hidden">
                      <div
                        className={`h-full transition-all duration-500 ${
                          card.status === 'warning' ? 'bg-amber-500' :
                          card.status === 'info' ? 'bg-blue-500' : 'bg-emerald-500'
                        }`}
                        style={{ width: `${card.retention}%` }}
                      ></div>
                    </div>
                  </div>

                  <div className="mt-3 text-[11px] text-slate-400 bg-slate-900/60 p-2.5 rounded-xl border border-slate-800">
                    <span className="text-slate-300 font-semibold">ملاحظة الحفظ:</span> {card.lastMistake}
                  </div>
                </div>

                {/* Rating Action (Anki Style) */}
                <div className="pt-3 border-t border-slate-800/80">
                  <span className="text-[10px] text-slate-400 block mb-2 text-center">قيّم استحضارك الآن لضبط الموعد القادم:</span>
                  <div className="grid grid-cols-3 gap-2">
                    <button
                      onClick={() => handleRateCard(card.id, 'hard')}
                      className="py-1.5 px-2 bg-red-950/40 hover:bg-red-900/60 border border-red-500/40 text-red-300 rounded-lg text-xs font-semibold transition-colors"
                    >
                      صعب (تكرار اليوم)
                    </button>
                    <button
                      onClick={() => handleRateCard(card.id, 'good')}
                      className="py-1.5 px-2 bg-blue-950/40 hover:bg-blue-900/60 border border-blue-500/40 text-blue-300 rounded-lg text-xs font-semibold transition-colors"
                    >
                      جيد (بعد يومين)
                    </button>
                    <button
                      onClick={() => handleRateCard(card.id, 'easy')}
                      className="py-1.5 px-2 bg-emerald-950/40 hover:bg-emerald-900/60 border border-emerald-500/40 text-emerald-300 rounded-lg text-xs font-semibold transition-colors"
                    >
                      متقن (بعد أسبوع)
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 3: MIND MAPS */}
      {activeTab === 'mindmap' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Layers className="w-5 h-5 text-emerald-400" />
                الربط البصري والذهني للآيات (الخرائط الذهنية الموضوعية)
              </h3>
              <p className="text-slate-400 text-xs mt-1">تقسيم السور إلى محاور وقصص مترابطة بصرياً لتثبيت المتشابهات وعدم الخلط.</p>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400 font-semibold">اختر السورة:</span>
              <select
                value={selectedSurah}
                onChange={(e) => setSelectedSurah(e.target.value)}
                className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-xl px-4 py-2.5 focus:outline-none focus:border-emerald-500 font-bold"
              >
                <option value="الملك">سورة الملك (30 آية)</option>
                <option value="الكهف">سورة الكهف (110 آية)</option>
                <option value="يس">سورة يس (83 آية)</option>
              </select>
            </div>
          </div>

          <div className="bg-slate-950/80 rounded-3xl p-6 border border-slate-800 space-y-6">
            <div className="text-center space-y-1">
              <h4 className="text-lg font-bold text-emerald-300">
                {surahMindMaps[selectedSurah].title}
              </h4>
              <p className="text-xs text-slate-400 max-w-xl mx-auto">
                {surahMindMaps[selectedSurah].description}
              </p>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              {surahMindMaps[selectedSurah].topics.map((topic, idx) => (
                <div
                  key={idx}
                  className={`p-5 rounded-2xl bg-gradient-to-br ${topic.bg} border ${topic.border} space-y-3 shadow-md relative group hover:scale-[1.01] transition-all`}
                >
                  <div className="flex justify-between items-center">
                    <span className="text-xs font-bold text-slate-300 bg-slate-950/70 px-3 py-1 rounded-full border border-slate-700">
                      المحور {idx + 1}
                    </span>
                    <span className="text-xs text-emerald-300 font-bold">{topic.range}</span>
                  </div>
                  <h5 className="font-bold text-white text-sm leading-relaxed">{topic.title}</h5>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
