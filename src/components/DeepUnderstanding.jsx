import React, { useState } from 'react';
import { HeartPulse, Search, BookOpen, Compass, Info, Sparkles, MessageSquareQuote, Check, Layers } from 'lucide-react';

export default function DeepUnderstanding() {
  const [activeTab, setActiveTab] = useState('semantic'); // 'semantic', 'contextCard'
  const [userFeeling, setUserFeeling] = useState('');
  const [searchResult, setSearchResult] = useState(null);

  // Selected Verse for Context Card
  const [selectedContextVerse, setSelectedContextVerse] = useState('duha');

  // Preset Feelings
  const feelingPresets = [
    "أشعر بقلق وتوتر من المستقبل والرزق",
    "تعرضت للظلم وأشعر بضيق شديد في صدري",
    "أشعر بتقصير وذنب وأبحث عن باب التوبة",
    "أمر بابتلاء واختبار صعب في حياتي",
    "أبحث عن الطمأنينة والسلام الداخلي والسكينة"
  ];

  const contextVerses = {
    duha: {
      title: "سورة الضُّحَى - الآية 3",
      era: "عهد مَكّي 🕋",
      order: "ترتيب النزول: 11",
      verse: "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ",
      story: "احتبس الوحي عن النبي ﷺ أربعة عشر يوماً، فقال المشركون: 'إن محمداً قد ودّعه ربه وقلاه (أبغضه)'، فأنزل الله هذه السورة تسلية لنبيه ﷺ ونفياً قاطعاً لما زعموه وتطميناً لقلبه الشريف.",
      meanings: [
        { word: "مَا وَدَّعَكَ", mean: "ما تركك وما قطعك منذ أن اصطفاك." },
        { word: "وَمَا قَلَىٰ", mean: "ما أبغضك ولا كرهك قط، بل أنت حبيب الرحمن." },
        { word: "سَجَىٰ", mean: "سكن واشتد ظلامه وركد." }
      ],
      lesson: "مهما طالت فترات الصمت أو تأخر الفرج، فإن معية الله ولطفه بالمؤمن لا تنقطع."
    },
    inshirah: {
      title: "سورة الشَّرْح - الآية 5-6",
      era: "عهد مَكّي 🕋",
      order: "ترتيب النزول: 12",
      verse: "فَإِنَّ مَعَ ٱلْعُسْرِ يُسْرًا * إِنَّ مَعَ ٱلْعُسْرِ يُسْرًا",
      story: "نزلت في وقت كان النبي ﷺ والمسلمون الأوائل يواجهون شدة واستضعافاً في مكة، فبشرهم الله بأن مع كل شدة مفردة يُسرين عظيمين محيطين بها.",
      meanings: [
        { word: "ٱلْعُسْرِ", mean: "الشدة والضيق وجاءت معرفة بأل (عسر واحد)." },
        { word: "يُسْرًا", mean: "التيسير والفرج وجاءت منكرة للدلالة على السعة والكثرة (يسران)." },
        { word: "وِزْرَكَ", mean: "حِمْلك الثقيل من هموم الرسالة." }
      ],
      lesson: "لن يغلب عسر واحد يسرين؛ فالمعاناة يصاحبها الفرج في نفس الأوان."
    },
    hujurat: {
      title: "سورة الحُجُرَات - الآية 6",
      era: "عهد مَدَني 🕌",
      order: "ترتيب النزول: 106",
      verse: "يَٰٓأَيُّهَا ٱلَّذِينَ ءَامَنُوٓا۟ إِن جَآءَكُمْ فَاسِقٌۢ بِنَبَإٍ فَتَبَيَّنُوٓا۟ أَن تُصِيبُوا۟ قَوْمًۢا بِجَهَٰلَةٍ فَتُصْبِحُوا۟ عَلَىٰ مَا فَعَلْتُمْ نَٰدِمِينَ",
      story: "نزلت حين بعث النبي ﷺ الوليد بن عقبة مصدقاً إلى بني المصطلق، فلما اقترب خاف فرجع وظن أنهم يريدون قتاله ونقل خبراً غير دقيق، فنزلت الآية تأمر بالتثبت والتحقق من الشائعات.",
      meanings: [
        { word: "فَتَبَيَّنُوا", mean: "تثبتوا وتحققوا من صحة الخبر قبل التصديق والنشر." },
        { word: "بِجَهَالَةٍ", mean: "بظن باطل وخطأ لا عن بصيرة وحقيقة." },
        { word: "نَادِمِينَ", mean: "متحسرين على إيقاع الأذى بالأبرياء." }
      ],
      lesson: "القاعدة الذهبية في العصر الرقمي: التحقق من مصادر الأخبار قبل إعادة ترويجها."
    }
  };

  const handleSemanticSearch = (query) => {
    const textToSearch = query || userFeeling;
    if (!textToSearch.trim()) return;

    setUserFeeling(textToSearch);

    // Mock Semantic Search Logic based on input keywords
    if (textToSearch.includes("قلق") || textToSearch.includes("رزق") || textToSearch.includes("مستقبل")) {
      setSearchResult({
        quran: {
          verse: "وَمَن يَتَّقِ ٱللَّهَ يَجْعَل لَّهُۥ مَخْرَجًا * وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ وَمَن يَتَوَكَّلْ عَلَى ٱللَّهِ فَهُوَ حَسْبُهُۥ",
          reference: "سورة الطلاق - الآية 2-3",
          reflection: "الرزق والطمأنينة مرتبطان ارتباطاً وثيقاً بالتقوى والتوكل الصادق. الآية تفيد بأن الله يفتح الأبواب المغلقة من حيث لا تدري ولا تخطط."
        },
        hadith: {
          text: "لَوْ أَنَّكُمْ كُنْتُمْ تَتَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
          reference: "سنن الترمذي - حديث صحيح",
          actionableAdvice: "استيقظ في الصباح وابذل أسبابك الساعية بكل اطمئنان، مع يقين بأن الرزق مقسوم لا يفوته أحد."
        }
      });
    } else if (textToSearch.includes("ظلم") || textToSearch.includes("ضيق")) {
      setSearchResult({
        quran: {
          verse: "فَٱصْبِرْ صَبْرًا جَمِيلًا * إِنَّهُمْ يَرَوْنَهُۥ بَعِيدًا * وَنَرَىٰهُ قَرِيبًا",
          reference: "سورة المعارج - الآية 5-7",
          reflection: "الصبر الجميل هو الصبر الذي لا شكوى فيه لغير الله، وثق بأن ميزان العدل الإلهي لا يُظلم فيه مثقال ذرة."
        },
        hadith: {
          text: "اتَّقِ دَعْوَةَ الْمَظْلُومِ، فَإِنَّهُ لَيْسَ بَيْنَهَا وَبَيْنَ اللَّهِ حِجَابٌ",
          reference: "صحيح البخاري",
          actionableAdvice: "احرص على ألا تظلم أحداً، واطمئن بأن مظلمتك محفوفة بعناية الله وقدرته النافذة."
        }
      });
    } else if (textToSearch.includes("تقصير") || textToSearch.includes("ذنب") || textToSearch.includes("توبة")) {
      setSearchResult({
        quran: {
          verse: "قُلْ يَٰعِبَادِيَ ٱلَّذِينَ أَسْرَفُوا۟ عَلَىٰٓ أَنفُسِهِمْ لَا تَقْنَطُوا۟ مِن رَّحْمَةِ ٱللَّهِ ۚ إِنَّ ٱللَّهَ يَغْفِرُ ٱلذُّنُوبَ جَمِيعًا",
          reference: "سورة الزمر - الآية 53",
          reflection: "أرجى آية في كتاب الله؛ نداء ملؤه اللطف والرحمة لمن أثقلتهم الخطايا أن باب الله مفتوح دائماً بلا وساطة."
        },
        hadith: {
          text: "كُلُّ ابْنِ آدَمَ خَطَّاءٌ وَخَيْرُ الْخَطَّائِينَ التَّوَّابُونَ",
          reference: "سنن الترمذي - حديث حسن",
          actionableAdvice: "قم بركعتي توبة هادئتين واستغفر الله 100 مرة الآن لتبدأ صفحة جديدة مع ربك."
        }
      });
    } else if (textToSearch.includes("ابتلاء") || textToSearch.includes("اختبار")) {
      setSearchResult({
        quran: {
          verse: "وَلَنَبْلُوَنَّكُم بِشَيْءٍ مِّنَ ٱلْخَوْفِ وَٱلْجُوعِ وَنَقْصٍ مِّنَ ٱلْأَمْوَٰلِ وَٱلْأَنفُسِ وَٱلثَّمَرَٰتِ ۗ وَبَشِّرِ ٱلصَّٰبِرِينَ",
          reference: "سورة البقرة - الآية 155",
          reflection: "الابتلاء سنة الحياة الدنيا للمؤمن، ووراء كل اختبار رفعة درجات وتطهير لصحيفة الأعمال وبشرى للمحتسبين."
        },
        hadith: {
          text: "مَا يُصِيبُ الْمُسْلِمَ مِنْ نَصَبٍ وَلا وَصَبٍ وَلا هَمٍّ وَلا حَزَنٍ وَلا أَذًى حَتَّى الشَّوْكَةِ يُشَاكُهَا إِلاَّ كَفَّرَ اللَّهُ بِهَا مِنْ خَطَايَاهُ",
          reference: "صحيح البخاري",
          actionableAdvice: "قل: 'إنا لله وإنا إليه راجعون، اللهم أجرني في مصيبتي وأخلف لي خيراً منها'."
        }
      });
    } else {
      setSearchResult({
        quran: {
          verse: "ٱلَّذِينَ ءَامَنُوا۟ وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ ٱللَّهِ ۗ أَلَا بِذِكْرِ ٱللَّهِ تَطْمَئِنُّ ٱلْقُلُوبُ",
          reference: "سورة الرعد - الآية 28",
          reflection: "القلب البشري لا يجد السكن والراحة الحقيقية إلا بالاتصال الدائم بخالقه سبحانه وتعالى."
        },
        hadith: {
          text: "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ لَهُ خَيْرٌ، وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ",
          reference: "صحيح مسلم",
          actionableAdvice: "خصص 10 دقائق بعد الصلاة للاستغفار والتسبيح في هدوء لتجديد السكينة الداخلية."
        }
      });
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-teal-900/80 via-emerald-800/50 to-slate-900 border border-teal-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-teal-500/20 text-teal-300 text-xs font-semibold mb-3 border border-teal-500/30">
              <HeartPulse className="w-3.5 h-3.5" />
              البحث الدلالي والنفسي
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المعايشة والفهم العميق</h2>
            <p className="text-slate-300 text-sm mt-1">
              ربط الحالة النفسية بآيات القرآن والسنة الشريفة، واستعراض التفسير السياقي والتاريخي التفاعلي.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('semantic')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'semantic' ? 'bg-teal-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الباحث النفسي
            </button>
            <button
              onClick={() => setActiveTab('contextCard')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'contextCard' ? 'bg-teal-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              بطاقة السياق والتفسير
            </button>
          </div>
        </div>
      </div>

      {/* TAB 1: SEMANTIC & EMOTIONAL SEARCH */}
      {activeTab === 'semantic' && (
        <div className="space-y-6">
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg">
            <h3 className="text-lg font-bold text-teal-300 mb-2 flex items-center gap-2">
              <Compass className="w-5 h-5 text-teal-400" />
              ماذا تشعر الآن؟ أو ما هو الموقف الذي يمر بك؟
            </h3>
            <p className="text-slate-400 text-xs mb-4">
              اكتب تعبيراً حرّاً عن مشاعرك أو ظرفك، وسيقوم المحرك الدلالي بربطك بالآيات والأحاديث النبوية والخطوات العملية التي تواسيك وتوجهك.
            </p>

            <div className="flex flex-col md:flex-row gap-3 mb-4">
              <div className="relative flex-1">
                <input
                  type="text"
                  value={userFeeling}
                  onChange={(e) => setUserFeeling(e.target.value)}
                  placeholder="مثال: أشعر بقلق وتوتر بخصوص رزقي أو خائف من اتخاذ قرار..."
                  className="w-full bg-slate-950 border border-slate-700 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-teal-500 pl-10"
                />
                <Search className="w-4 h-4 text-slate-500 absolute left-3 top-3.5" />
              </div>
              <button
                onClick={() => handleSemanticSearch()}
                className="px-6 py-3 bg-teal-600 hover:bg-teal-500 text-white text-sm font-semibold rounded-xl shadow-md transition-all flex items-center justify-center gap-2"
              >
                <Sparkles className="w-4 h-4" />
                استخراج التوجيه
              </button>
            </div>

            {/* Presets */}
            <div className="space-y-2">
              <span className="text-xs text-slate-400 font-semibold">أمثلة مقترحة للبحث الشعوري:</span>
              <div className="flex flex-wrap gap-2">
                {feelingPresets.map((preset, idx) => (
                  <button
                    key={idx}
                    onClick={() => handleSemanticSearch(preset)}
                    className="text-xs bg-slate-800/80 hover:bg-slate-700/80 text-teal-200/90 px-3 py-1.5 rounded-lg border border-slate-700 transition-colors"
                  >
                    {preset}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Results Box */}
          {searchResult && (
            <div className="grid md:grid-cols-2 gap-6 animate-fade-in">
              {/* Quranic Guidance */}
              <div className="bg-slate-900/90 border border-emerald-500/30 rounded-2xl p-6 shadow-lg space-y-4">
                <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                  <span className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                    <BookOpen className="w-4 h-4" />
                    المواساة من كتاب الله
                  </span>
                  <span className="text-xs text-slate-400 font-medium">{searchResult.quran.reference}</span>
                </div>
                <p className="text-xl text-emerald-100 font-serif leading-relaxed text-center py-2">
                  "{searchResult.quran.verse}"
                </p>
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
                  <span className="font-semibold text-emerald-400 block">💡 التوجيه النفسي والإيماني:</span>
                  <p className="leading-relaxed">{searchResult.quran.reflection}</p>
                </div>
              </div>

              {/* Sunnah & Practical Guidance */}
              <div className="bg-slate-900/90 border border-teal-500/30 rounded-2xl p-6 shadow-lg space-y-4">
                <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                  <span className="text-xs font-bold text-teal-400 flex items-center gap-1.5">
                    <MessageSquareQuote className="w-4 h-4" />
                    التوجيه النبوي الشريف
                  </span>
                  <span className="text-xs text-slate-400 font-medium">{searchResult.hadith.reference}</span>
                </div>
                <p className="text-base text-teal-100 font-serif leading-relaxed text-center py-2">
                  "{searchResult.hadith.text}"
                </p>
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
                  <span className="font-semibold text-teal-400 block flex items-center gap-1">
                    <Check className="w-3.5 h-3.5" />
                    خطوة عملية لتطبيقها في يومك:
                  </span>
                  <p className="leading-relaxed">{searchResult.hadith.actionableAdvice}</p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: INTERACTIVE CONTEXT CARD */}
      {activeTab === 'contextCard' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Info className="w-5 h-5 text-teal-400" />
                بطاقة السياق التفاعلية (بطاقة الآية وأسباب النزول)
              </h3>
              <p className="text-slate-400 text-xs">
                عرض سياق النزول، العهد (مكي/مدني)، المعاني البصرية للكلمات الغريبة، وأسباب النزول كقصة قصيرة.
              </p>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400">اختر الآية:</span>
              <select
                value={selectedContextVerse}
                onChange={(e) => setSelectedContextVerse(e.target.value)}
                className="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-xl px-3 py-2 focus:outline-none focus:border-teal-500"
              >
                <option value="duha">سورة الضحى (الآية 3)</option>
                <option value="inshirah">سورة الشرح (الآية 5-6)</option>
                <option value="hujurat">سورة الحجرات (الآية 6)</option>
              </select>
            </div>
          </div>

          {/* Sample Verse Context Card */}
          <div className="bg-gradient-to-b from-slate-950 to-slate-900 border border-teal-500/30 rounded-2xl p-6 space-y-6 shadow-2xl">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 pb-4">
              <div className="flex items-center gap-2">
                <span className="bg-teal-500/20 text-teal-300 text-xs px-3 py-1 rounded-full border border-teal-500/30 font-semibold">
                  {contextVerses[selectedContextVerse].title}
                </span>
                <span className="bg-purple-500/20 text-purple-300 text-xs px-2.5 py-1 rounded-full border border-purple-500/30 font-semibold">
                  {contextVerses[selectedContextVerse].era}
                </span>
              </div>
              <span className="text-xs text-slate-400">{contextVerses[selectedContextVerse].order}</span>
            </div>

            <div className="text-center py-4">
              <p className="text-2xl md:text-3xl font-serif text-teal-100 leading-loose">
                "{contextVerses[selectedContextVerse].verse}"
              </p>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              {/* Cause of Revelation */}
              <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800 space-y-2">
                <h4 className="text-xs font-bold text-teal-400 flex items-center gap-1.5">
                  📖 سبب النزول (قصة مختصرة موثقة):
                </h4>
                <p className="text-xs text-slate-300 leading-relaxed">
                  {contextVerses[selectedContextVerse].story}
                </p>
                <div className="pt-2 border-t border-slate-800/80 text-[11px] text-teal-300">
                  <span className="font-semibold text-slate-300">الأثر والتدبر:</span> {contextVerses[selectedContextVerse].lesson}
                </div>
              </div>

              {/* Visual Word Meanings */}
              <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800 space-y-2">
                <h4 className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                  🔍 معاني الكلمات الغريبة بصرياً:
                </h4>
                <ul className="text-xs text-slate-300 space-y-2">
                  {contextVerses[selectedContextVerse].meanings.map((m, idx) => (
                    <li key={idx} className="flex items-center justify-between bg-slate-950 p-2.5 rounded-lg border border-slate-800/80">
                      <span className="font-bold text-amber-300">{m.word}:</span>
                      <span className="text-slate-400 text-[11px]">{m.mean}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
