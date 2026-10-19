import React, { useState } from 'react';
import { Mic, MicOff, Volume2, CheckCircle2, AlertCircle, RefreshCw, BookOpen, Brain, Sparkles, ChevronLeft, ChevronRight, Layers } from 'lucide-react';

export default function QuranMemorization() {
  const [activeTab, setActiveTab] = useState('corrector'); // 'corrector', 'spaced', 'mindmap'

  // Audio Reciter State
  const [isRecording, setIsRecording] = useState(false);
  const [recordingResult, setRecordingResult] = useState(null);

  // Spaced Repetition State
  const [flashcards, setFlashcards] = useState([
    { id: 1, surah: 'سورة الملك', ayats: '1 - 5', level: 'يحتاج مراجعة قريبة', interval: 'بعد 6 ساعات', progress: 40, status: 'warning' },
    { id: 2, surah: 'سورة الكهف', ayats: '10 - 20', level: 'مستقر نسبياً', interval: 'غداً', progress: 75, status: 'info' },
    { id: 3, surah: 'سورة مريم', ayats: '1 - 15', level: 'ممتاز - حفظ متقن', interval: 'بعد 5 أيام', progress: 95, status: 'success' },
  ]);

  // Mind Map State
  const [selectedSurah, setSelectedSurah] = useState('الملك');

  const handleStartRecording = () => {
    setIsRecording(true);
    setRecordingResult(null);
    setTimeout(() => {
      setIsRecording(false);
      setRecordingResult({
        text: "تَبَٰرَكَ ٱلَّذِي بِيَدِهِ ٱلْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
        tajweedNote: "تنبيه تجويدي: انتبه لمقدار مد الموصل في 'شَيْءٍ قَدِيرٌ' إخفاء حقيقي عند القاف بغنة مفخمة.",
        accuracy: 94,
        mistakes: [
          { word: "قَدِيرٌ", type: "تجويد", detail: "تفخيم غنة الإخفاء لأن القاف حرف استعلاء." }
        ]
      });
    }, 3000);
  };

  const surahMindMaps = {
    'الملك': {
      title: 'سورة الملك - خريطة المحاور الموضوعية',
      topics: [
        { title: 'عظمة القدرة الإلهية وخلق الموت والحياة', range: 'الآيات 1 - 5', bg: 'from-emerald-900/60 to-emerald-800/40', border: 'border-emerald-500/40' },
        { title: 'عاقبة المكذبين ومصير أصحاب السعير', range: 'الآيات 6 - 12', bg: 'from-amber-900/60 to-amber-800/40', border: 'border-amber-500/40' },
        { title: 'شمول علم الله وبديع خلقه للأرض والسماء', range: 'الآيات 13 - 19', bg: 'from-blue-900/60 to-blue-800/40', border: 'border-blue-500/40' },
        { title: 'التذكير بالنعم وتحذير العباد من العذاب', range: 'الآيات 20 - 30', bg: 'from-purple-900/60 to-purple-800/40', border: 'border-purple-500/40' },
      ]
    },
    'الكهف': {
      title: 'سورة الكهف - خريطة الفتن الأربع والنجاة منها',
      topics: [
        { title: 'فتنة الدين - قصة أصحاب الكهف', range: 'الآيات 9 - 26', bg: 'from-emerald-900/60 to-emerald-800/40', border: 'border-emerald-500/40' },
        { title: 'فتنة المال - قصة صاحب الجنتين', range: 'الآيات 32 - 44', bg: 'from-amber-900/60 to-amber-800/40', border: 'border-amber-500/40' },
        { title: 'فتنة العلم - قصة موسى والخضر', range: 'الآيات 60 - 82', bg: 'from-blue-900/60 to-blue-800/40', border: 'border-blue-500/40' },
        { title: 'فتنة السلطة - قصة ذي القرنين', range: 'الآيات 83 - 98', bg: 'from-purple-900/60 to-purple-800/40', border: 'border-purple-500/40' },
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
                <span className="text-xs bg-slate-800 text-slate-300 px-2.5 py-1 rounded-full border border-slate-700">سورة الملك (الآية 1)</span>
              </div>
              <p className="text-slate-300 text-sm mb-6 leading-relaxed">
                اضغط على الزر واقرأ الآية بصوتك، وسيقوم الذكاء الاصطناعي بتحليل المخارج والتشكيل وأحكام التجويد فوراً.
              </p>

              {/* Record Button UI */}
              <div className="flex flex-col items-center justify-center my-8">
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
                <p className="text-xs text-slate-400 mt-4">
                  {isRecording ? "جاري الاستماع للآية ومطابقتها بالتجويد..." : "انقر لبدء التسميع والتصحيح"}
                </p>
              </div>
            </div>

            <div className="bg-slate-950/60 rounded-xl p-4 border border-slate-800 text-xs text-slate-400">
              💡 <span className="text-slate-300 font-semibold">ملاحظة تقنية:</span> تعتمد الدقة العالية المباشرة للتجويد الفعلي على خوادم مخصصة لمعالجة الصوت ومخارج الحروف.
            </div>
          </div>

          {/* AI Result View */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg flex flex-col justify-between">
            <div>
              <h3 className="text-lg font-bold text-slate-200 mb-4 flex items-center gap-2">
                <Brain className="w-5 h-5 text-emerald-400" />
                نتيجة التقييم والملاحظات
              </h3>

              {recordingResult ? (
                <div className="space-y-4">
                  <div className="p-4 bg-slate-950 rounded-xl border border-emerald-500/30">
                    <span className="text-xs text-emerald-400 font-semibold block mb-1">الآية المكتشفة:</span>
                    <p className="text-lg text-emerald-200 font-serif leading-loose text-center">
                      "{recordingResult.text}"
                    </p>
                  </div>

                  <div className="flex items-center justify-between bg-slate-800/50 p-3 rounded-lg border border-slate-700">
                    <span className="text-xs text-slate-300">نسبة مطابقة الحفظ والتجويد:</span>
                    <span className="text-sm font-bold text-emerald-400">{recordingResult.accuracy}%</span>
                  </div>

                  <div className="p-4 bg-amber-950/30 border border-amber-500/30 rounded-xl text-xs space-y-2">
                    <div className="flex items-center gap-2 text-amber-400 font-semibold">
                      <AlertCircle className="w-4 h-4" />
                      تنبيه الأحكام التجويدية:
                    </div>
                    <p className="text-amber-200/90 leading-relaxed">
                      {recordingResult.tajweedNote}
                    </p>
                  </div>
                </div>
              ) : (
                <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-xl p-6 text-center text-slate-500">
                  <Volume2 className="w-12 h-12 mb-3 opacity-30" />
                  <p className="text-sm">لم يتم إجراء تسميع بعد.</p>
                  <p className="text-xs text-slate-600 mt-1">اضغط على الميكروفون لبدء تجربة المصحح الذكي.</p>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-end">
              <button className="px-4 py-2 text-xs bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg transition-colors border border-slate-700 flex items-center gap-1.5">
                <RefreshCw className="w-3.5 h-3.5" />
                إعادة المحاولة
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: SPACED REPETITION */}
      {activeTab === 'spaced' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div>
            <h3 className="text-xl font-bold text-white mb-2 flex items-center gap-2">
              <BookOpen className="w-5 h-5 text-emerald-400" />
              خطة المراجعة بنظام التكرار المتباعد (Spaced Repetition)
            </h3>
            <p className="text-slate-400 text-sm">
              يحلل النظام آلياً مستويات الأخطاء السابقة، ويحدد بدقة الأوقات المثالية لمراجعة كل مقطع قبل أن يدخل في مرحلة النسيان.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-4">
            {flashcards.map((card) => (
              <div key={card.id} className="bg-slate-950 border border-slate-800 rounded-xl p-5 hover:border-emerald-500/40 transition-all shadow-md relative overflow-hidden group">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <h4 className="font-bold text-white text-base">{card.surah}</h4>
                    <span className="text-xs text-slate-400">الآيات: {card.ayats}</span>
                  </div>
                  <span className={`text-xs px-2.5 py-1 rounded-full font-medium ${
                    card.status === 'warning' ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30' :
                    card.status === 'info' ? 'bg-blue-500/20 text-blue-300 border border-blue-500/30' :
                    'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                  }`}>
                    {card.interval}
                  </span>
                </div>

                <div className="space-y-2 mt-4">
                  <div className="flex justify-between text-xs text-slate-400">
                    <span>قوة الحفظ:</span>
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
                </div>

                <div className="mt-5 pt-3 border-t border-slate-800/80 flex items-center justify-between text-xs">
                  <span className="text-slate-400">{card.level}</span>
                  <button className="text-emerald-400 hover:text-emerald-300 font-medium flex items-center gap-1 group-hover:translate-x-0.5 transition-transform">
                    بدء المراجعة
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
                الربط البصري والذهني للآيات (الخرائط الموضوعية)
              </h3>
              <p className="text-slate-400 text-sm">تقسيم السور موضوعياً للمساعدة في تثبيت تسلسل الأفكار والآيات المتشابهة.</p>
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

          <div className="bg-slate-950/80 rounded-2xl p-6 border border-slate-800">
            <h4 className="text-base font-bold text-emerald-300 mb-6 text-center border-b border-slate-800 pb-3">
              {surahMindMaps[selectedSurah].title}
            </h4>

            <div className="grid md:grid-cols-2 gap-4">
              {surahMindMaps[selectedSurah].topics.map((topic, idx) => (
                <div key={idx} className={`p-4 rounded-xl bg-gradient-to-br ${topic.bg} border ${topic.border} space-y-2 shadow-md relative group hover:scale-[1.01] transition-all`}>
                  <div className="flex justify-between items-center">
                    <span className="text-xs font-bold text-slate-300 bg-slate-950/50 px-2.5 py-0.5 rounded-full border border-slate-700">
                      المحور {idx + 1}
                    </span>
                    <span className="text-xs text-emerald-300 font-medium">{topic.range}</span>
                  </div>
                  <h5 className="font-semibold text-white text-sm">{topic.title}</h5>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
