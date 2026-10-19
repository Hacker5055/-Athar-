/* ═══════════════════════════════════════════════════════════════
   data.js — محتوى تجريبي للبروتوتايب
   ⚠️ النصوص القرآنية مكتوبة بالرسم العثماني (رواية حفص).
      بعض السور الطويلة فيها عيّنة آيات فقط (البروتوتايب مش منتج نهائي).
   ═══════════════════════════════════════════════════════════════ */

/* ───────────────── المدن ───────────────── */
const CITIES = [
  { id:'cairo',    ar:'القاهرة',    en:'Cairo',       fr:'Le Caire',   lat:30.0444, lng:31.2357, tz:'Africa/Cairo' },
  { id:'alex',     ar:'الإسكندرية', en:'Alexandria',  fr:'Alexandrie', lat:31.2001, lng:29.9187, tz:'Africa/Cairo' },
  { id:'riyadh',   ar:'الرياض',     en:'Riyadh',      fr:'Riyad',      lat:24.7136, lng:46.6753, tz:'Asia/Riyadh' },
  { id:'makkah',   ar:'مكة المكرمة', en:'Makkah',     fr:'La Mecque',  lat:21.3891, lng:39.8579, tz:'Asia/Riyadh' },
  { id:'madina',   ar:'المدينة المنورة', en:'Madinah', fr:'Médine',    lat:24.4686, lng:39.6142, tz:'Asia/Riyadh' },
  { id:'dubai',    ar:'دبي',        en:'Dubai',       fr:'Dubaï',      lat:25.2048, lng:55.2708, tz:'Asia/Dubai' },
  { id:'casa',     ar:'الدار البيضاء', en:'Casablanca', fr:'Casablanca', lat:33.5731, lng:-7.5898, tz:'Africa/Casablanca' },
  { id:'alger',    ar:'الجزائر',    en:'Algiers',     fr:'Alger',      lat:36.7538, lng:3.0588,  tz:'Africa/Algiers' },
  { id:'tunis',    ar:'تونس',       en:'Tunis',       fr:'Tunis',      lat:36.8065, lng:10.1815, tz:'Africa/Tunis' },
  { id:'paris',    ar:'باريس',      en:'Paris',       fr:'Paris',      lat:48.8566, lng:2.3522,  tz:'Europe/Paris' },
  { id:'lyon',     ar:'ليون',       en:'Lyon',        fr:'Lyon',       lat:45.7640, lng:4.8357,  tz:'Europe/Paris' },
  { id:'london',   ar:'لندن',       en:'London',      fr:'Londres',    lat:51.5072, lng:-0.1276, tz:'Europe/London' },
  { id:'istanbul', ar:'إستنبول',    en:'Istanbul',    fr:'Istanbul',   lat:41.0082, lng:28.9784, tz:'Europe/Istanbul' },
  { id:'kl',       ar:'كوالالمبور', en:'Kuala Lumpur', fr:'Kuala Lumpur', lat:3.1390, lng:101.6869, tz:'Asia/Kuala_Lumpur' },
  { id:'ny',       ar:'نيويورك',    en:'New York',    fr:'New York',   lat:40.7128, lng:-74.0060, tz:'America/New_York' },
];

/* ───────────────── السور (١١٤) ───────────────── */
const SURAHS = [
  [1,'الفاتحة','Al-Fatiha','Al-Fatiha',7,'m'],[2,'البقرة','Al-Baqarah','Al-Baqarah',286,'M'],[3,'آل عمران',"Al 'Imran","Al 'Imran",200,'M'],
  [4,'النساء','An-Nisa','An-Nisa',176,'M'],[5,'المائدة','Al-Ma’idah','Al-Ma’ida',120,'M'],[6,'الأنعام','Al-An’am','Al-An’am',165,'m'],
  [7,'الأعراف','Al-A’raf','Al-A’raf',206,'m'],[8,'الأنفال','Al-Anfal','Al-Anfal',75,'M'],[9,'التوبة','At-Tawbah','At-Tawba',129,'M'],
  [10,'يونس','Yunus','Yunus',109,'m'],[11,'هود','Hud','Hud',123,'m'],[12,'يوسف','Yusuf','Yusuf',111,'m'],
  [13,'الرعد','Ar-Ra’d','Ar-Ra’d',43,'M'],[14,'إبراهيم','Ibrahim','Ibrahim',52,'m'],[15,'الحجر','Al-Hijr','Al-Hijr',99,'m'],
  [16,'النحل','An-Nahl','An-Nahl',128,'m'],[17,'الإسراء','Al-Isra','Al-Isra',111,'m'],[18,'الكهف','Al-Kahf','Al-Kahf',110,'m'],
  [19,'مريم','Maryam','Maryam',98,'m'],[20,'طه','Ta-Ha','Ta-Ha',135,'m'],[21,'الأنبياء','Al-Anbiya','Al-Anbiya',112,'m'],
  [22,'الحج','Al-Hajj','Al-Hajj',78,'M'],[23,'المؤمنون','Al-Mu’minun','Al-Mu’minun',118,'m'],[24,'النور','An-Nur','An-Nur',64,'M'],
  [25,'الفرقان','Al-Furqan','Al-Furqan',77,'m'],[26,'الشعراء','Ash-Shu’ara','Ash-Shu’ara',227,'m'],[27,'النمل','An-Naml','An-Naml',93,'m'],
  [28,'القصص','Al-Qasas','Al-Qasas',88,'m'],[29,'العنكبوت','Al-’Ankabut','Al-’Ankabut',69,'m'],[30,'الروم','Ar-Rum','Ar-Rum',60,'m'],
  [31,'لقمان','Luqman','Luqman',34,'m'],[32,'السجدة','As-Sajdah','As-Sajda',30,'m'],[33,'الأحزاب','Al-Ahzab','Al-Ahzab',73,'M'],
  [34,'سبأ','Saba','Saba',54,'m'],[35,'فاطر','Fatir','Fatir',45,'m'],[36,'يس','Ya-Sin','Ya-Sin',83,'m'],
  [37,'الصافات','As-Saffat','As-Saffat',182,'m'],[38,'ص','Sad','Sad',88,'m'],[39,'الزمر','Az-Zumar','Az-Zumar',75,'m'],
  [40,'غافر','Ghafir','Ghafir',85,'m'],[41,'فصلت','Fussilat','Fussilat',54,'m'],[42,'الشورى','Ash-Shura','Ash-Shura',53,'m'],
  [43,'الزخرف','Az-Zukhruf','Az-Zukhruf',89,'m'],[44,'الدخان','Ad-Dukhan','Ad-Dukhan',59,'m'],[45,'الجاثية','Al-Jathiyah','Al-Jathiya',37,'m'],
  [46,'الأحقاف','Al-Ahqaf','Al-Ahqaf',35,'m'],[47,'محمد','Muhammad','Muhammad',38,'M'],[48,'الفتح','Al-Fath','Al-Fath',29,'M'],
  [49,'الحجرات','Al-Hujurat','Al-Hujurat',18,'M'],[50,'ق','Qaf','Qaf',45,'m'],[51,'الذاريات','Adh-Dhariyat','Adh-Dhariyat',60,'m'],
  [52,'الطور','At-Tur','At-Tur',49,'m'],[53,'النجم','An-Najm','An-Najm',62,'m'],[54,'القمر','Al-Qamar','Al-Qamar',55,'m'],
  [55,'الرحمن','Ar-Rahman','Ar-Rahman',78,'M'],[56,'الواقعة','Al-Waqi’ah','Al-Waqi’a',96,'m'],[57,'الحديد','Al-Hadid','Al-Hadid',29,'M'],
  [58,'المجادلة','Al-Mujadila','Al-Mujadila',22,'M'],[59,'الحشر','Al-Hashr','Al-Hashr',24,'M'],[60,'الممتحنة','Al-Mumtahanah','Al-Mumtahana',13,'M'],
  [61,'الصف','As-Saff','As-Saff',14,'M'],[62,'الجمعة','Al-Jumu’ah','Al-Jumu’a',11,'M'],[63,'المنافقون','Al-Munafiqun','Al-Munafiqun',11,'M'],
  [64,'التغابن','At-Taghabun','At-Taghabun',18,'M'],[65,'الطلاق','At-Talaq','At-Talaq',12,'M'],[66,'التحريم','At-Tahrim','At-Tahrim',12,'M'],
  [67,'الملك','Al-Mulk','Al-Mulk',30,'m'],[68,'القلم','Al-Qalam','Al-Qalam',52,'m'],[69,'الحاقة','Al-Haqqah','Al-Haqqa',52,'m'],
  [70,'المعارج','Al-Ma’arij','Al-Ma’arij',44,'m'],[71,'نوح','Nuh','Nuh',28,'m'],[72,'الجن','Al-Jinn','Al-Jinn',28,'m'],
  [73,'المزمل','Al-Muzzammil','Al-Muzzammil',20,'m'],[74,'المدثر','Al-Muddaththir','Al-Muddaththir',56,'m'],[75,'القيامة','Al-Qiyamah','Al-Qiyama',40,'m'],
  [76,'الإنسان','Al-Insan','Al-Insan',31,'M'],[77,'المرسلات','Al-Mursalat','Al-Mursalat',50,'m'],[78,'النبأ','An-Naba','An-Naba',40,'m'],
  [79,'النازعات','An-Nazi’at','An-Nazi’at',46,'m'],[80,'عبس','’Abasa','’Abasa',42,'m'],[81,'التكوير','At-Takwir','At-Takwir',29,'m'],
  [82,'الانفطار','Al-Infitar','Al-Infitar',19,'m'],[83,'المطففين','Al-Mutaffifin','Al-Mutaffifin',36,'m'],[84,'الانشقاق','Al-Inshiqaq','Al-Inshiqaq',25,'m'],
  [85,'البروج','Al-Buruj','Al-Buruj',22,'m'],[86,'الطارق','At-Tariq','At-Tariq',17,'m'],[87,'الأعلى','Al-A’la','Al-A’la',19,'m'],
  [88,'الغاشية','Al-Ghashiyah','Al-Ghashiya',26,'m'],[89,'الفجر','Al-Fajr','Al-Fajr',30,'m'],[90,'البلد','Al-Balad','Al-Balad',20,'m'],
  [91,'الشمس','Ash-Shams','Ash-Shams',15,'m'],[92,'الليل','Al-Layl','Al-Layl',21,'m'],[93,'الضحى','Ad-Duha','Ad-Duha',11,'m'],
  [94,'الشرح','Ash-Sharh','Ash-Sharh',8,'m'],[95,'التين','At-Tin','At-Tin',8,'m'],[96,'العلق','Al-’Alaq','Al-’Alaq',19,'m'],
  [97,'القدر','Al-Qadr','Al-Qadr',5,'m'],[98,'البينة','Al-Bayyinah','Al-Bayyina',8,'M'],[99,'الزلزلة','Az-Zalzalah','Az-Zalzala',8,'M'],
  [100,'العاديات','Al-’Adiyat','Al-’Adiyat',11,'m'],[101,'القارعة','Al-Qari’ah','Al-Qari’a',11,'m'],[102,'التكاثر','At-Takathur','At-Takathur',8,'m'],
  [103,'العصر','Al-’Asr','Al-’Asr',3,'m'],[104,'الهمزة','Al-Humazah','Al-Humaza',9,'m'],[105,'الفيل','Al-Fil','Al-Fil',5,'m'],
  [106,'قريش','Quraysh','Quraysh',4,'m'],[107,'الماعون','Al-Ma’un','Al-Ma’un',7,'m'],[108,'الكوثر','Al-Kawthar','Al-Kawthar',3,'m'],
  [109,'الكافرون','Al-Kafirun','Al-Kafirun',6,'m'],[110,'النصر','An-Nasr','An-Nasr',3,'M'],[111,'المسد','Al-Masad','Al-Masad',5,'m'],
  [112,'الإخلاص','Al-Ikhlas','Al-Ikhlas',4,'m'],[113,'الفلق','Al-Falaq','Al-Falaq',5,'m'],[114,'الناس','An-Nas','An-Nas',6,'m'],
].map(r => ({ n:r[0], ar:r[1], en:r[2], fr:r[3], v:r[4], type:r[5]==='m'?'meccan':'medinan' }));

/* ───────────────── نصوص الآيات (عيّنة) ───────────────── */
const AYAHS = {
  1:[
    'بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ',
    'ٱلۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينَ',
    'ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ',
    'مَٰلِكِ يَوۡمِ ٱلدِّينِ',
    'إِيَّاكَ نَعۡبُدُ وَإِيَّاكَ نَسۡتَعِينُ',
    'ٱهۡدِنَا ٱلصِّرَٰطَ ٱلۡمُسۡتَقِيمَ',
    'صِرَٰطَ ٱلَّذِينَ أَنۡعَمۡتَ عَلَيۡهِمۡ غَيۡرِ ٱلۡمَغۡضُوبِ عَلَيۡهِمۡ وَلَا ٱلضَّآلِّينَ'
  ],
  2:[
    'الٓمٓ','ذَٰلِكَ ٱلۡكِتَٰبُ لَا رَيۡبَۛ فِيهِۛ هُدٗى لِّلۡمُتَّقِينَ',
    'ٱلَّذِينَ يُؤۡمِنُونَ بِٱلۡغَيۡبِ وَيُقِيمُونَ ٱلصَّلَوٰةَ وَمِمَّا رَزَقۡنَٰهُمۡ يُنفِقُونَ',
    'وَٱلَّذِينَ يُؤۡمِنُونَ بِمَآ أُنزِلَ إِلَيۡكَ وَمَآ أُنزِلَ مِن قَبۡلِكَ وَبِٱلۡأٓخِرَةِ هُمۡ يُوقِنُونَ',
    'أُوْلَٰٓئِكَ عَلَىٰ هُدٗى مِّن رَّبِّهِمۡۖ وَأُوْلَٰٓئِكَ هُمُ ٱلۡمُفۡلِحُونَ'
  ],
  18:[
    'ٱلۡحَمۡدُ لِلَّهِ ٱلَّذِيٓ أَنزَلَ عَلَىٰ عَبۡدِهِ ٱلۡكِتَٰبَ وَلَمۡ يَجۡعَل لَّهُۥ عِوَجَاۜ',
    'قَيِّمٗا لِّيُنذِرَ بَأۡسٗا شَدِيدٗا مِّن لَّدُنۡهُ وَيُبَشِّرَ ٱلۡمُؤۡمِنِينَ ٱلَّذِينَ يَعۡمَلُونَ ٱلصَّٰلِحَٰتِ أَنَّ لَهُمۡ أَجۡرًا حَسَنٗا',
    'مَّٰكِثِينَ فِيهِ أَبَدٗا',
    'وَيُنذِرَ ٱلَّذِينَ قَالُواْ ٱتَّخَذَ ٱللَّهُ وَلَدٗا',
    'مَّا لَهُم بِهِۦ مِنۡ عِلۡمٖ وَلَا لِأٓبَآئِهِمۡۚ كَبُرَتۡ كَلِمَةٗ تَخۡرُجُ مِنۡ أَفۡوَٰهِهِمۡۚ إِن يَقُولُونَ إِلَّا كَذِبٗا',
    'فَلَعَلَّكَ بَٰخِعٞ نَّفۡسَكَ عَلَىٰٓ ءَاثَٰرِهِمۡ إِن لَّمۡ يُؤۡمِنُواْ بِهَٰذَا ٱلۡحَدِيثِ أَسَفًا',
    'إِنَّا جَعَلۡنَا مَا عَلَى ٱلۡأَرۡضِ زِينَةٗ لَّهَا لِنَبۡلُوَهُمۡ أَيُّهُمۡ أَحۡسَنُ عَمَلٗا',
    'وَإِنَّا لَجَٰعِلُونَ مَا عَلَيۡهَا صَعِيدٗا جُرُزًا',
    'أَمۡ حَسِبۡتَ أَنَّ أَصۡحَٰبَ ٱلۡكَهۡفِ وَٱلرَّقِيمِ كَانُواْ مِنۡ ءَايَٰتِنَا عَجَبًا',
    'إِذۡ أَوَى ٱلۡفِتۡيَةُ إِلَى ٱلۡكَهۡفِ فَقَالُواْ رَبَّنَآ ءَاتِنَا مِن لَّدُنكَ رَحۡمَةٗ وَهَيِّئۡ لَنَا مِنۡ أَمۡرِنَا رَشَدٗا'
  ],
  36:[
    'يسٓ','وَٱلۡقُرۡءَانِ ٱلۡحَكِيمِ','إِنَّكَ لَمِنَ ٱلۡمُرۡسَلِينَ','عَلَىٰ صِرَٰطٖ مُّسۡتَقِيمٖ',
    'تَنزِيلَ ٱلۡعَزِيزِ ٱلرَّحِيمِ','لِتُنذِرَ قَوۡمٗا مَّآ أُنذِرَ ءَابَآؤُهُمۡ فَهُمۡ غَٰفِلُونَ',
    'لَقَدۡ حَقَّ ٱلۡقَوۡلُ عَلَىٰٓ أَكۡثَرِهِمۡ فَهُمۡ لَا يُؤۡمِنُونَ',
    'إِنَّا جَعَلۡنَا فِيٓ أَعۡنَٰقِهِمۡ أَغۡلَٰلٗا فَهِيَ إِلَى ٱلۡأَذۡقَانِ فَهُم مُّقۡمَحُونَ',
    'وَجَعَلۡنَا مِنۢ بَيۡنِ أَيۡدِيهِمۡ سَدّٗا وَمِنۡ خَلۡفِهِمۡ سَدّٗا فَأَغۡشَيۡنَٰهُمۡ فَهُمۡ لَا يُبۡصِرُونَ',
    'وَسَوَآءٌ عَلَيۡهِمۡ ءَأَنذَرۡتَهُمۡ أَمۡ لَمۡ تُنذِرۡهُمۡ لَا يُؤۡمِنُونَ',
    'إِنَّمَا تُنذِرُ مَنِ ٱتَّبَعَ ٱلذِّكۡرَ وَخَشِيَ ٱلرَّحۡمَٰنَ بِٱلۡغَيۡبِۖ فَبَشِّرۡهُ بِمَغۡفِرَةٖ وَأَجۡرٖ كَرِيمٍ',
    'إِنَّا نَحۡنُ نُحۡيِ ٱلۡمَوۡتَىٰ وَنَكۡتُبُ مَا قَدَّمُواْ وَءَاثَٰرَهُمۡۚ وَكُلَّ شَيۡءٍ أَحۡصَيۡنَٰهُ فِيٓ إِمَامٖ مُّبِينٖ'
  ],
  55:[
    'ٱلرَّحۡمَٰنُ','عَلَّمَ ٱلۡقُرۡءَانَ','خَلَقَ ٱلۡإِنسَٰنَ','عَلَّمَهُ ٱلۡبَيَانَ',
    'ٱلشَّمۡسُ وَٱلۡقَمَرُ بِحُسۡبَانٖ','وَٱلنَّجۡمُ وَٱلشَّجَرُ يَسۡجُدَانِ',
    'وَٱلسَّمَآءَ رَفَعَهَا وَوَضَعَ ٱلۡمِيزَانَ','أَلَّا تَطۡغَوۡاْ فِي ٱلۡمِيزَانِ',
    'وَأَقِيمُواْ ٱلۡوَزۡنَ بِٱلۡقِسۡطِ وَلَا تُخۡسِرُواْ ٱلۡمِيزَانَ','وَٱلۡأَرۡضَ وَضَعَهَا لِلۡأَنَامِ'
  ],
  67:[
    'تَبَٰرَكَ ٱلَّذِي بِيَدِهِ ٱلۡمُلۡكُ وَهُوَ عَلَىٰ كُلِّ شَيۡءٖ قَدِيرٌ',
    'ٱلَّذِي خَلَقَ ٱلۡمَوۡتَ وَٱلۡحَيَوٰةَ لِيَبۡلُوَكُمۡ أَيُّكُمۡ أَحۡسَنُ عَمَلٗاۚ وَهُوَ ٱلۡعَزِيزُ ٱلۡغَفُورُ',
    'ٱلَّذِي خَلَقَ سَبۡعَ سَمَٰوَٰتٖ طِبَاقٗاۖ مَّا تَرَىٰ فِي خَلۡقِ ٱلرَّحۡمَٰنِ مِن تَفَٰوُتٖۖ فَٱرۡجِعِ ٱلۡبَصَرَ هَلۡ تَرَىٰ مِن فُطُورٖ',
    'ثُمَّ ٱرۡجِعِ ٱلۡبَصَرَ كَرَّتَيۡنِ يَنقَلِبۡ إِلَيۡكَ ٱلۡبَصَرُ خَاسِئٗا وَهُوَ حَسِيرٞ',
    'وَلَقَدۡ زَيَّنَّا ٱلسَّمَآءَ ٱلدُّنۡيَا بِمَصَٰبِيحَ وَجَعَلۡنَٰهَا رُجُومٗا لِّلشَّيَٰطِينِۖ وَأَعۡتَدۡنَا لَهُمۡ عَذَابَ ٱلسَّعِيرِ'
  ],
  103:['وَٱلۡعَصۡرِ','إِنَّ ٱلۡإِنسَٰنَ لَفِي خُسۡرٍ','إِلَّا ٱلَّذِينَ ءَامَنُواْ وَعَمِلُواْ ٱلصَّٰلِحَٰتِ وَتَوَاصَوۡاْ بِٱلۡحَقِّ وَتَوَاصَوۡاْ بِٱلصَّبۡرِ'],
  108:['إِنَّآ أَعۡطَيۡنَٰكَ ٱلۡكَوۡثَرَ','فَصَلِّ لِرَبِّكَ وَٱنۡحَرۡ','إِنَّ شَانِئَكَ هُوَ ٱلۡأَبۡتَرُ'],
  109:[
    'قُلۡ يَٰٓأَيُّهَا ٱلۡكَٰفِرُونَ','لَآ أَعۡبُدُ مَا تَعۡبُدُونَ','وَلَآ أَنتُمۡ عَٰبِدُونَ مَآ أَعۡبُدُ',
    'وَلَآ أَنَا۠ عَابِدٞ مَّا عَبَدتُّمۡ','وَلَآ أَنتُمۡ عَٰبِدُونَ مَآ أَعۡبُدُ','لَكُمۡ دِينُكُمۡ وَلِيَ دِينِ'
  ],
  110:['إِذَا جَآءَ نَصۡرُ ٱللَّهِ وَٱلۡفَتۡحُ','وَرَأَيۡتَ ٱلنَّاسَ يَدۡخُلُونَ فِي دِينِ ٱللَّهِ أَفۡوَاجٗا','فَسَبِّحۡ بِحَمۡدِ رَبِّكَ وَٱسۡتَغۡفِرۡهُۚ إِنَّهُۥ كَانَ تَوَّابَۢا'],
  111:['تَبَّتۡ يَدَآ أَبِي لَهَبٖ وَتَبَّ','مَآ أَغۡنَىٰ عَنۡهُ مَالُهُۥ وَمَا كَسَبَ','سَيَصۡلَىٰ نَارٗا ذَاتَ لَهَبٖ','وَٱمۡرَأَتُهُۥ حَمَّالَةَ ٱلۡحَطَبِ','فِي جِيدِهَا حَبۡلٞ مِّن مَّسَدِۢ'],
  112:['قُلۡ هُوَ ٱللَّهُ أَحَدٌ','ٱللَّهُ ٱلصَّمَدُ','لَمۡ يَلِدۡ وَلَمۡ يُولَدۡ','وَلَمۡ يَكُن لَّهُۥ كُفُوًا أَحَدُۢ'],
  113:['قُلۡ أَعُوذُ بِرَبِّ ٱلۡفَلَقِ','مِن شَرِّ مَا خَلَقَ','وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ','وَمِن شَرِّ ٱلنَّفَّٰثَٰتِ فِي ٱلۡعُقَدِ','وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ'],
  114:['قُلۡ أَعُوذُ بِرَبِّ ٱلنَّاسِ','مَلِكِ ٱلنَّاسِ','إِلَٰهِ ٱلنَّاسِ','مِن شَرِّ ٱلۡوَسۡوَاسِ ٱلۡخَنَّاسِ','ٱلَّذِي يُوَسۡوِسُ فِي صُدُورِ ٱلنَّاسِ','مِنَ ٱلۡجِنَّةِ وَٱلنَّاسِ']
};

/* آية الكرسي تُعرض داخل سورة البقرة كآية ٢٥٥ (عيّنة منفصلة) */
const AYAT_AL_KURSI =
  'ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلۡحَيُّ ٱلۡقَيُّومُۚ لَا تَأۡخُذُهُۥ سِنَةٞ وَلَا نَوۡمٞۚ لَّهُۥ مَا فِي ٱلسَّمَٰوَٰتِ وَمَا فِي ٱلۡأَرۡضِۗ مَن ذَا ٱلَّذِي يَشۡفَعُ عِندَهُۥٓ إِلَّا بِإِذۡنِهِۦۚ يَعۡلَمُ مَا بَيۡنَ أَيۡدِيهِمۡ وَمَا خَلۡفَهُمۡۖ وَلَا يُحِيطُونَ بِشَيۡءٖ مِّنۡ عِلۡمِهِۦٓ إِلَّا بِمَا شَآءَۚ وَسِعَ كُرۡسِيُّهُ ٱلسَّمَٰوَٰتِ وَٱلۡأَرۡضَۖ وَلَا يَـُٔودُهُۥ حِفۡظُهُمَاۚ وَهُوَ ٱلۡعَلِيُّ ٱلۡعَظِيمُ';

/* ───────────────── الترجمات (عيّنة) ───────────────── */
const TRANS = {
  1:{ en:[
    'In the name of Allah, the Most Gracious, the Most Merciful.',
    'All praise is for Allah, Lord of all worlds,',
    'the Most Gracious, the Most Merciful,',
    'Master of the Day of Judgment.',
    'You alone we worship, and You alone we ask for help.',
    'Guide us to the straight path,',
    'the path of those You have blessed, not of those who incurred Your wrath, nor of those who went astray.'],
    fr:[
    'Au nom d’Allah, le Tout Miséricordieux, le Très Miséricordieux.',
    'Louange à Allah, Seigneur de l’univers,',
    'le Tout Miséricordieux, le Très Miséricordieux,',
    'Maître du Jour du Jugement.',
    'C’est Toi que nous adorons et c’est Toi dont nous implorons le secours.',
    'Guide-nous dans le droit chemin,',
    'le chemin de ceux que Tu as comblés, non pas celui de ceux qui ont encouru Ta colère ni des égarés.']},
  103:{ en:['By time,','Indeed, mankind is in loss,','except those who believe, do righteous deeds, urge one another to truth, and urge one another to patience.'],
    fr:['Par le Temps !','L’homme est certes en perdition,','excepté ceux qui croient, accomplissent les bonnes œuvres, s’enjoignent mutuellement la vérité et s’enjoignent mutuellement la patience.']},
  108:{ en:['Indeed, We have granted you abundant goodness.','So pray to your Lord and sacrifice.','Indeed, your enemy is the one cut off.'],
    fr:['Nous t’avons certes accordé l’abondance.','Accomplis donc la prière pour ton Seigneur et sacrifie.','Celui qui te hait, c’est lui qui sera sans postérité.']},
  109:{ en:['Say, “O disbelievers!','I do not worship what you worship,','nor do you worship what I worship.','I will never worship what you worship,','nor will you ever worship what I worship.','You have your religion, and I have mine.”'],
    fr:['Dis : « Ô vous les mécréants !','Je n’adore pas ce que vous adorez,','et vous n’adorez pas ce que j’adore.','Je n’adorerai jamais ce que vous adorez,','et vous n’adorerez jamais ce que j’adore.','À vous votre religion, et à moi ma religion. »']},
  110:{ en:['When the victory of Allah has come and the conquest,','and you see people entering the religion of Allah in multitudes,','then glorify the praises of your Lord and seek His forgiveness. Indeed, He is ever Accepting of repentance.'],
    fr:['Lorsque vient le secours d’Allah et la victoire,','et que tu vois les gens entrer en foule dans la religion d’Allah,','alors célèbre les louanges de ton Seigneur et implore Son pardon. Il est certes Grand Accueillant au repentir.']},
  112:{ en:['Say, “He is Allah, the One.','Allah, the Eternal Refuge.','He neither begets nor is born,','nor is there to Him any equivalent.”'],
    fr:['Dis : « Il est Allah, Unique.','Allah, Le Seul à être imploré.','Il n’a jamais engendré, n’a pas été engendré,','et nul n’est égal à Lui. »']},
  113:{ en:['Say, “I seek refuge in the Lord of daybreak,','from the evil of what He created,','and from the evil of darkness when it settles,','and from the evil of the blowers in knots,','and from the evil of the envier when he envies.”'],
    fr:['Dis : « Je cherche refuge auprès du Seigneur de l’aube naissante,','contre le mal de ce qu’Il a créé,','contre le mal de l’obscurité quand elle s’étend,','contre le mal de celles qui soufflent sur les nœuds,','et contre le mal de l’envieux quand il envie. »']},
  114:{ en:['Say, “I seek refuge in the Lord of mankind,','the Sovereign of mankind,','the God of mankind,','from the evil of the retreating whisperer,','who whispers into the hearts of mankind,','from among jinn and mankind.”'],
    fr:['Dis : « Je cherche refuge auprès du Seigneur des hommes,','le Souverain des hommes,','la Divinité des hommes,','contre le mal du mauvais conseiller, furtif,','qui souffle le mal dans les cœurs des hommes,','qu’il soit djinn ou être humain. »']},
  2:{ en:[
    'Alif, Lām, Mīm.','This is the Book about which there is no doubt, a guidance for those conscious of Allah,',
    'who believe in the unseen, establish prayer, and spend out of what We have provided for them,',
    'and who believe in what has been revealed to you and what was revealed before you, and of the Hereafter they are certain.',
    'It is they who are upon guidance from their Lord, and it is they who are the successful.'],
    fr:[
    'Alif, Lām, Mīm.','C’est le Livre au sujet duquel il n’y a aucun doute : un guide pour les pieux,',
    'ceux qui croient à l’invisible, accomplissent la prière et dépensent de ce que Nous leur avons attribué,',
    'ceux qui croient à ce qui t’a été révélé et à ce qui a été révélé avant toi, et qui croient fermement à l’au-delà.',
    'Ceux-là suivent la guidée de leur Seigneur, et ce sont eux qui réussissent.']},
  36:{ en:[
    'Yā, Sīn.','By the wise Quran,','indeed you are from among the messengers,','on a straight path.',
    'A revelation of the Almighty, the Most Merciful,','that you may warn a people whose forefathers were not warned, so they are unaware.',
    'The word has already come into effect upon most of them, so they do not believe.',
    'Indeed, We have put shackles on their necks reaching their chins, so their heads are forced up.',
    'And We have put a barrier before them and a barrier behind them, and covered them, so they do not see.',
    'It is all the same to them whether you warn them or do not warn them — they will not believe.',
    'You can only warn one who follows the message and fears the Most Merciful unseen. So give him good tidings of forgiveness and a noble reward.',
    'Indeed, it is We who bring the dead to life and record what they have put forth and what they left behind, and all things We have enumerated in a clear register.'],
    fr:[
    'Yā, Sīn.','Par le Coran plein de sagesse !','Tu es certes du nombre des Messagers,','sur un chemin droit.',
    'C’est une révélation du Tout-Puissant, du Très Miséricordieux,','afin que tu avertisses un peuple dont les ancêtres n’ont pas été avertis et qui sont donc insouciants.',
    'La Parole s’est déjà réalisée contre la plupart d’entre eux : ils ne croiront donc pas.',
    'Nous avons mis à leurs cous des carcans jusqu’aux mentons : voilà qu’ils marchent la tête levée.',
    'Et Nous avons placé devant eux une barrière et derrière eux une barrière, et Nous les avons couverts : ils ne voient donc rien.',
    'Il est indifférent, pour eux, que tu les avertisses ou non : ils ne croiront pas.',
    'Tu avertis seulement celui qui suit le Rappel et craint le Tout Miséricordieux quoiqu’il ne Le voie pas. Annonce-lui donc un pardon et une récompense généreuse.',
    'C’est Nous qui redonnons la vie aux morts et qui consignons ce qu’ils ont accompli ainsi que leurs traces. Et Nous avons recensé toute chose dans un registre explicite.']},
  55:{ en:[
    'The Most Merciful,','taught the Quran,','created mankind,','and taught them clear expression.',
    'The sun and the moon follow precise calculation,','and the stars and the trees prostrate.',
    'He raised the sky and set the balance,','so that you do not transgress the balance.',
    'Establish weight with justice and do not fall short in the balance.','And He laid out the earth for all living beings.'],
    fr:[
    'Le Tout Miséricordieux,','a enseigné le Coran,','a créé l’homme,','et lui a appris à s’exprimer clairement.',
    'Le soleil et la lune évoluent selon un calcul exact,','et les étoiles et les arbres se prosternent.',
    'Il a élevé le ciel et établi la balance,','afin que vous ne transgressiez pas la balance.',
    'Donnez le juste poids et ne faussez pas la balance.','Et Il a étendu la terre pour les créatures.']},
  67:{ en:[
    'Blessed is He in whose hand is dominion, and He is over all things competent,',
    'He who created death and life to test you as to which of you is best in deed. And He is the Almighty, the Forgiving,',
    'He who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency. So return your vision — do you see any flaw?',
    'Then return your vision twice again. Your vision will return to you humbled while it is fatigued.',
    'And We have certainly beautified the nearest heaven with lamps and have made them missiles for the devils, and We have prepared for them the punishment of the Blaze.'],
    fr:[
    'Béni soit Celui dans la main de Qui se trouve la royauté, et Il est Omnipotent,',
    'Celui qui a créé la mort et la vie afin de vous éprouver quant à celui d’entre vous qui accomplira les meilleures œuvres. Et Il est le Tout-Puissant, le Pardonneur,',
    'Celui qui a créé sept cieux superposés. Tu ne vois aucune disproportion dans la création du Tout Miséricordieux. Ramène donc ton regard : y vois-tu une brèche ?',
    'Puis, ramène le regard à deux reprises : ton regard reviendra vers toi humilié et épuisé.',
    'Nous avons certes embelli le ciel le plus proche de lampes et en avons fait des projectiles contre les démons, et Nous leur avons préparé le châtiment de la Fournaise.']},
  18:{ en:[
    'All praise is for Allah who has revealed to His Servant the Book and has not made in it any deviance.',
    'Upright, to warn of a severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward,',
    'in which they will remain forever,','and to warn those who say, “Allah has taken a son.”',
    'They have no knowledge of it, nor did their forefathers. Grave is the word that comes out of their mouths; they speak not except a lie.',
    'Then perhaps you would kill yourself with grief over them if they do not believe in this message.',
    'Indeed, We have made that which is on the earth adornment for it, that We may test them as to which of them is best in deed.',
    'And indeed, We will make that which is upon it a barren ground.',
    'Or have you thought that the companions of the cave and the inscription were, among Our signs, a wonder?',
    'When the youths retreated to the cave and said, “Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.”'],
    fr:[
    'Louange à Allah qui a révélé le Livre à Son serviteur et n’y a point mis de tortuosité.',
    'Un Livre droit, pour avertir d’un châtiment sévère de Sa part et annoncer aux croyants qui accomplissent les bonnes œuvres une belle récompense,',
    'où ils demeureront éternellement,','et pour avertir ceux qui disent : « Allah s’est attribué un fils. »',
    'Ils n’en ont aucune science, pas plus que leurs ancêtres. Qu’elle est grave la parole qui sort de leurs bouches ! Ils ne disent que mensonge.',
    'Tu vas peut-être te consumer de chagrin pour eux, s’ils ne croient pas à ce récit.',
    'Nous avons certes fait de ce qui est sur terre une parure, afin de les éprouver et de voir qui d’entre eux accomplira les meilleures œuvres.',
    'Et Nous ferons certes de ce qui s’y trouve un sol aride.',
    'Ou as-tu pensé que les gens de la caverne et d’ar-Raqīm étaient, parmi Nos signes, une chose prodigieuse ?',
    'Quand les jeunes gens se réfugièrent dans la caverne et dirent : « Notre Seigneur, accorde-nous de Ta part une miséricorde et assure-nous la droiture dans notre affaire. »']}
};

/* ───────────────── التفسير المختصر (عيّنة) ───────────────── */
const TAFSIR = {
  1:{
    1: 'بدأ الله كتابه بالبسملة تعليمًا لعباده أن يستفتحوا أعمالهم بذكره، وفيها التبرك باسمه سبحانه واستمداد العون منه.',
    2: 'الحمد الكامل لله وحده؛ فهو المستحق للثناء على كمال ذاته وصفاته وأفعاله، وهو ربّ كل مخلوق ومربيه بنعمه.',
    3: 'الرحمن: وسعت رحمته كل شيء. الرحيم: يرحم المؤمنين خاصة يوم القيامة. والجمع بينهما دليل على سعة الرحمة وخصوصها.',
    4: 'مالك يوم القيامة وحده، يوم الجزاء والحساب، لا ملك فيه لأحد سواه.',
    5: 'نخصّك وحدك بالعبادة، ونطلب العون منك وحدك؛ فالعبادة لا تصرف إلا لله، والاستعانة لا تطلب إلا منه.',
    6: 'اهدنا وثبّتنا على الطريق المستقيم، وهو الإسلام، ووفّقنا لسلوكه ظاهرًا وباطنًا.',
    7: 'طريق الذين أنعمت عليهم من النبيين والصديقين والشهداء والصالحين، لا طريق المغضوب عليهم ولا الضالين.'
  },
  36:{
    12: 'نحيي الموتى يوم القيامة ونبعثهم، ونكتب في صحفهم ما قدّموه في حياتهم من أعمال، وما تركوه بعد موتهم من آثار حسنة أو سيئة؛ فالآثار محسوبة كالأعمال. وكل شيء مكتوب ومحفوظ عند الله في اللوح المحفوظ.'
  },
  18:{
    6: 'لا تهلك نفسك حزنًا عليهم يا محمد إن لم يؤمنوا بهذا القرآن؛ فأنت بلّغت، والهداية بيد الله.',
    10: 'دعاء الفتية حين اعتزلوا قومهم: ربنا أعطنا رحمة من عندك تثبّتنا بها، وهيّئ لنا من أمرنا رشدًا وسدادًا في كل ما نأتي ونذر.'
  },
  67:{
    2: 'خلق الله الموت والحياة ليختبر عباده: أيهم أحسن عملًا؟ وأحسن العمل أخلصه لله وأصوبه على سنة رسوله ﷺ.'
  },
  112:{
    1: 'قل يا محمد: هو الله المتفرّد بالألوهية والربوبية والأسماء والصفات، أحد في ذاته وصفاته وأفعاله.',
    4: 'ولم يكن له مثيل ولا شبيه ولا كفء في شيء من صفات الكمال.'
  }
};

/* ───────────────── الأذكار ───────────────── */
const ADHKAR = [
  { id:'morning', icon:'🌅', items:[
    { text:'ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلۡحَيُّ ٱلۡقَيُّومُۚ لَا تَأۡخُذُهُۥ سِنَةٞ وَلَا نَوۡمٞۚ لَّهُۥ مَا فِي ٱلسَّمَٰوَٰتِ وَمَا فِي ٱلۡأَرۡضِۗ مَن ذَا ٱلَّذِي يَشۡفَعُ عِندَهُۥٓ إِلَّا بِإِذۡنِهِۦۚ يَعۡلَمُ مَا بَيۡنَ أَيۡدِيهِمۡ وَمَا خَلۡفَهُمۡۖ وَلَا يُحِيطُونَ بِشَيۡءٖ مِّنۡ عِلۡمِهِۦٓ إِلَّا بِمَا شَآءَۚ وَسِعَ كُرۡسِيُّهُ ٱلسَّمَٰوَٰتِ وَٱلۡأَرۡضَۖ وَلَا يَـُٔودُهُۥ حِفۡظُهُمَاۚ وَهُوَ ٱلۡعَلِيُّ ٱلۡعَظِيمُ', count:1, ref:'آية الكرسي · من قرأها حين يصبح أُجير من الجن حتى يمسي' },
    { text:'قُلۡ هُوَ ٱللَّهُ أَحَدٌ ۝ ٱللَّهُ ٱلصَّمَدُ ۝ لَمۡ يَلِدۡ وَلَمۡ يُولَدۡ ۝ وَلَمۡ يَكُن لَّهُۥ كُفُوًا أَحَدُۢ\n… والفلق … والناس', count:3, ref:'أبو داود والترمذي · «حسبك من كل شيء»' },
    { text:'أَصۡبَحۡنَا وَأَصۡبَحَ ٱلۡمُلۡكُ لِلَّهِ، وَٱلۡحَمۡدُ لِلَّهِ، وَلَآ إِلَٰهَ إِلَّا ٱللَّهُ وَحۡدَهُۥ لَا شَرِيكَ لَهُۥ، لَهُ ٱلۡمُلۡكُ وَلَهُ ٱلۡحَمۡدُ وَهُوَ عَلَىٰ كُلِّ شَيۡءٖ قَدِيرٞ. رَبِّ أَسۡأَلُكَ خَيۡرَ مَا فِي هَٰذَا ٱلۡيَوۡمِ وَخَيۡرَ مَا بَعۡدَهُۥ، وَأَعُوذُ بِكَ مِن شَرِّ مَا فِي هَٰذَا ٱلۡيَوۡمِ وَشَرِّ مَا بَعۡدَهُۥ', count:1, ref:'صحيح مسلم' },
    { text:'سُبۡحَانَ ٱللَّهِ وَبِحَمۡدِهِۦ', count:100, ref:'صحيح مسلم · من قالها مئة مرة حُطّت خطاياه' },
    { text:'لَآ إِلَٰهَ إِلَّا ٱللَّهُ وَحۡدَهُۥ لَا شَرِيكَ لَهُۥ، لَهُ ٱلۡمُلۡكُ وَلَهُ ٱلۡحَمۡدُ، وَهُوَ عَلَىٰ كُلِّ شَيۡءٖ قَدِيرٞ', count:10, ref:'أبو داود والترمذي · حسن' },
    { text:'أَعُوذُ بِكَلِمَاتِ ٱللَّهِ ٱلتَّآمَّاتِ مِن شَرِّ مَا خَلَقَ', count:3, ref:'صحيح مسلم' },
    { text:'بِسۡمِ ٱللَّهِ ٱلَّذِي لَا يَضُرُّ مَعَ ٱسۡمِهِۦ شَيۡءٞ فِي ٱلۡأَرۡضِ وَلَا فِي ٱلسَّمَآءِ وَهُوَ ٱلسَّمِيعُ ٱلۡعَلِيمُ', count:3, ref:'أبو داود والترمذي · صحيح' },
    { text:'رَضِيتُ بِٱللَّهِ رَبّٗا، وَبِٱلۡإِسۡلَٰمِ دِينٗا، وَبِمُحَمَّدٖ صَلَّى ٱللَّهُ عَلَيۡهِ وَسَلَّمَ نَبِيّٗا', count:3, ref:'أحمد وأبو داود · حسن' }
  ]},
  { id:'evening', icon:'🌇', items:[
    { text:'أَمۡسَيۡنَا وَأَمۡسَى ٱلۡمُلۡكُ لِلَّهِ، وَٱلۡحَمۡدُ لِلَّهِ، وَلَآ إِلَٰهَ إِلَّا ٱللَّهُ وَحۡدَهُۥ لَا شَرِيكَ لَهُۥ، لَهُ ٱلۡمُلۡكُ وَلَهُ ٱلۡحَمۡدُ وَهُوَ عَلَىٰ كُلِّ شَيۡءٖ قَدِيرٞ', count:1, ref:'صحيح مسلم' },
    { text:'ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ ٱلۡحَيُّ ٱلۡقَيُّومُ … (آية الكرسي)', count:1, ref:'من قرأها حين يمسي أُجير من الجن حتى يصبح' },
    { text:'قُلۡ هُوَ ٱللَّهُ أَحَدٌ … والفلق … والناس', count:3, ref:'أبو داود والترمذي' },
    { text:'سُبۡحَانَ ٱللَّهِ وَبِحَمۡدِهِۦ', count:100, ref:'صحيح مسلم' },
    { text:'ٱللَّهُمَّ عَافِنِي فِي بَدَنِي، ٱللَّهُمَّ عَافِنِي فِي سَمۡعِي، ٱللَّهُمَّ عَافِنِي فِي بَصَرِي، لَآ إِلَٰهَ إِلَّا أَنۡتَ', count:3, ref:'أبو داود · حسن' },
    { text:'أَعُوذُ بِكَلِمَاتِ ٱللَّهِ ٱلتَّآمَّاتِ مِن شَرِّ مَا خَلَقَ', count:3, ref:'صحيح مسلم' }
  ]},
  { id:'after_prayer', icon:'🕌', items:[
    { text:'أَسۡتَغۡفِرُ ٱللَّهَ', count:3, ref:'صحيح مسلم' },
    { text:'ٱللَّهُمَّ أَنۡتَ ٱلسَّلَٰمُ وَمِنكَ ٱلسَّلَٰمُ، تَبَارَكۡتَ يَا ذَا ٱلۡجَلَٰلِ وَٱلۡإِكۡرَامِ', count:1, ref:'صحيح مسلم' },
    { text:'سُبۡحَانَ ٱللَّهِ', count:33, ref:'صحيح مسلم' },
    { text:'ٱلۡحَمۡدُ لِلَّهِ', count:33, ref:'صحيح مسلم' },
    { text:'ٱللَّهُ أَكۡبَرُ', count:34, ref:'صحيح مسلم' },
    { text:'آية الكرسي', count:1, ref:'النسائي · «من قرأها دبر كل صلاة مكتوبة لم يمنعه من دخول الجنة إلا أن يموت»' }
  ]},
  { id:'sleep', icon:'🌙', items:[
    { text:'آية الكرسي', count:1, ref:'صحيح البخاري · «لن يزال عليك من الله حافظ»' },
    { text:'بِٱسۡمِكَ رَبِّي وَضَعۡتُ جَنۡبِي، وَبِكَ أَرۡفَعُهُۥ، فَإِنۡ أَمۡسَكۡتَ نَفۡسِي فَٱرۡحَمۡهَا، وَإِنۡ أَرۡسَلۡتَهَا فَٱحۡفَظۡهَا بِمَا تَحۡفَظُ بِهِۦ عِبَادَكَ ٱلصَّٰلِحِينَ', count:1, ref:'البخاري ومسلم' },
    { text:'ٱللَّهُمَّ قِنِي عَذَابَكَ يَوۡمَ تَبۡعَثُ عِبَادَكَ', count:3, ref:'أبو داود والترمذي' },
    { text:'سُبۡحَانَ ٱللَّهِ (٣٣) · ٱلۡحَمۡدُ لِلَّهِ (٣٣) · ٱللَّهُ أَكۡبَرُ (٣٤)', count:1, ref:'البخاري ومسلم' }
  ]},
  { id:'wake', icon:'☀️', items:[
    { text:'ٱلۡحَمۡدُ لِلَّهِ ٱلَّذِي أَحۡيَانَا بَعۡدَ مَآ أَمَاتَنَا وَإِلَيۡهِ ٱلنُّشُورُ', count:1, ref:'صحيح البخاري' },
    { text:'لَآ إِلَٰهَ إِلَّا ٱللَّهُ وَحۡدَهُۥ لَا شَرِيكَ لَهُۥ، لَهُ ٱلۡمُلۡكُ وَلَهُ ٱلۡحَمۡدُ، وَهُوَ عَلَىٰ كُلِّ شَيۡءٖ قَدِيرٞ', count:10, ref:'أبو داود · حسن' }
  ]},
  { id:'anxiety', icon:'🫂', items:[
    { text:'لَآ إِلَٰهَ إِلَّا ٱللَّهُ ٱلۡعَظِيمُ ٱلۡحَلِيمُ، لَآ إِلَٰهَ إِلَّا ٱللَّهُ رَبُّ ٱلۡعَرۡشِ ٱلۡعَظِيمِ، لَآ إِلَٰهَ إِلَّا ٱللَّهُ رَبُّ ٱلسَّمَٰوَٰتِ وَرَبُّ ٱلۡأَرۡضِ وَرَبُّ ٱلۡعَرۡشِ ٱلۡكَرِيمِ', count:1, ref:'البخاري ومسلم' },
    { text:'يَا حَيُّ يَا قَيُّومُ بِرَحۡمَتِكَ أَسۡتَغِيثُ', count:1, ref:'الترمذي · حسن' },
    { text:'حَسۡبِيَ ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ عَلَيۡهِ تَوَكَّلۡتُ وَهُوَ رَبُّ ٱلۡعَرۡشِ ٱلۡعَظِيمِ', count:7, ref:'أبو داود · صحيح' },
    { text:'ٱللَّهُمَّ إِنِّي عَبۡدُكَ، ٱبۡنُ عَبۡدِكَ، ٱبۡنُ أَمَتِكَ، نَاصِيَتِي بِيَدِكَ، مَاضٖ فِيَّ حُكۡمُكَ، عَدۡلٞ فِيَّ قَضَآؤُكَ، أَسۡأَلُكَ بِكُلِّ ٱسۡمٍ هُوَ لَكَ…', count:1, ref:'أحمد · صحيح' }
  ]},
  { id:'food', icon:'🍽️', items:[
    { text:'بِسۡمِ ٱللَّهِ', count:1, ref:'البخاري ومسلم' },
    { text:'ٱلۡحَمۡدُ لِلَّهِ ٱلَّذِي أَطۡعَمَنِي هَٰذَا وَرَزَقَنِيهِ مِنۡ غَيۡرِ حَوۡلٖ مِّنِّي وَلَا قُوَّةٖ', count:1, ref:'أبو داود والترمذي' },
    { text:'ٱلۡحَمۡدُ لِلَّهِ حَمۡدٗا كَثِيرٗا طَيِّبٗا مُّبَارَكٗا فِيهِ، غَيۡرَ مَكۡفِيّٖ وَلَا مُوَدَّعٖ وَلَا مُسۡتَغۡنًى عَنۡهُ رَبَّنَا', count:1, ref:'صحيح البخاري' }
  ]},
  { id:'mosque', icon:'🕋', items:[
    { text:'ٱللَّهُمَّ ٱفۡتَحۡ لِيٓ أَبۡوَابَ رَحۡمَتِكَ', count:1, ref:'صحيح مسلم · عند الدخول' },
    { text:'ٱللَّهُمَّ إِنِّيٓ أَسۡأَلُكَ مِن فَضۡلِكَ', count:1, ref:'صحيح مسلم · عند الخروج' },
    { text:'ٱللَّهُمَّ ٱجۡعَلۡ فِي قَلۡبِي نُورٗا، وَفِي لِسَانِي نُورٗا، وَاجۡعَلۡ مِنۡ أَمَامِي نُورٗا، وَمِنۡ خَلۡفِي نُورٗا', count:1, ref:'البخاري ومسلم · الذهاب إلى المسجد' }
  ]},
  { id:'travel', icon:'🚗', items:[
    { text:'سُبۡحَانَ ٱلَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُۥ مُقۡرِنِينَ، وَإِنَّآ إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ', count:1, ref:'صحيح مسلم' },
    { text:'ٱللَّهُ أَكۡبَرُ، ٱللَّهُ أَكۡبَرُ، ٱللَّهُ أَكۡبَرُ، سُبۡحَانَ ٱلَّذِي سَخَّرَ لَنَا هَٰذَا…', count:3, ref:'صحيح مسلم · عند الركوب' }
  ]},
  { id:'rain', icon:'🌧️', items:[
    { text:'ٱللَّهُمَّ صَيِّبٗا نَافِعٗا', count:1, ref:'صحيح البخاري' },
    { text:'مُطِرۡنَا بِفَضۡلِ ٱللَّهِ وَرَحۡمَتِهِۦ', count:1, ref:'البخاري ومسلم' }
  ]}
];

/* ───────────────── الأدعية ───────────────── */
const DUAS = [
  { ar:'رَبَّنَآ ءَاتِنَا فِي ٱلدُّنۡيَا حَسَنَةٗ وَفِي ٱلۡأٓخِرَةِ حَسَنَةٗ وَقِنَا عَذَابَ ٱلنَّارِ', en:'Our Lord, give us good in this world and good in the Hereafter, and protect us from the punishment of the Fire.', fr:'Notre Seigneur, accorde-nous une belle part ici-bas et une belle part dans l’au-delà, et protège-nous du châtiment du Feu.', ref:'البقرة ٢٠١' },
  { ar:'رَبَّنَا لَا تُؤَاخِذۡنَآ إِن نَّسِينَآ أَوۡ أَخۡطَأۡنَا', en:'Our Lord, do not hold us accountable if we forget or make a mistake.', fr:'Notre Seigneur, ne nous tiens pas rigueur si nous oublions ou commettons une erreur.', ref:'البقرة ٢٨٦' },
  { ar:'رَبَّنَا لَا تُزِغۡ قُلُوبَنَا بَعۡدَ إِذۡ هَدَيۡتَنَا وَهَبۡ لَنَا مِن لَّدُنكَ رَحۡمَةً', en:'Our Lord, let not our hearts deviate after You have guided us, and grant us mercy from Yourself.', fr:'Notre Seigneur, ne fais pas dévier nos cœurs après que Tu nous as guidés, et accorde-nous de Ta part une miséricorde.', ref:'آل عمران ٨' },
  { ar:'رَبَّنَا هَبۡ لَنَا مِنۡ أَزۡوَٰجِنَا وَذُرِّيَّٰتِنَا قُرَّةَ أَعۡيُنٖ وَٱجۡعَلۡنَا لِلۡمُتَّقِينَ إِمَامًا', en:'Our Lord, grant us from our spouses and offspring comfort to our eyes and make us leaders of the righteous.', fr:'Notre Seigneur, fais de nos épouses et de nos descendants la joie de nos yeux, et fais de nous un exemple pour les pieux.', ref:'الفرقان ٧٤' },
  { ar:'رَبِّ ٱشۡرَحۡ لِي صَدۡرِي وَيَسِّرۡ لِيٓ أَمۡرِي', en:'My Lord, expand for me my breast and ease for me my task.', fr:'Seigneur, ouvre-moi la poitrine et facilite-moi ma tâche.', ref:'طه ٢٥-٢٦' },
  { ar:'رَبِّ إِنِّي لِمَآ أَنزَلۡتَ إِلَيَّ مِنۡ خَيۡرٖ فَقِيرٞ', en:'My Lord, indeed I am in need of whatever good You send down to me.', fr:'Seigneur, je suis certes dans le besoin du bien que Tu feras descendre vers moi.', ref:'القصص ٢٤' },
  { ar:'حَسۡبِيَ ٱللَّهُ لَآ إِلَٰهَ إِلَّا هُوَ عَلَيۡهِ تَوَكَّلۡتُ وَهُوَ رَبُّ ٱلۡعَرۡشِ ٱلۡعَظِيمِ', en:'Allah is sufficient for me; there is no god but He. In Him I have put my trust, and He is Lord of the Mighty Throne.', fr:'Allah me suffit. Nulle divinité en dehors de Lui. En Lui je place ma confiance, et Il est le Seigneur du Trône immense.', ref:'التوبة ١٢٩' },
  { ar:'ٱللَّهُمَّ إِنِّيٓ أَعُوذُ بِكَ مِنَ ٱلۡهَمِّ وَٱلۡحُزۡنِ، وَٱلۡعَجۡزِ وَٱلۡكَسَلِ، وَٱلۡجُبۡنِ وَٱلۡبُخۡلِ، وَضَلَعِ ٱلدَّيۡنِ وَغَلَبَةِ ٱلرِّجَالِ', en:'O Allah, I seek refuge in You from worry and grief, from incapacity and laziness, from cowardice and stinginess, from being overcome by debt and overpowered by men.', fr:'Ô Allah, je cherche refuge auprès de Toi contre le souci et la tristesse, l’incapacité et la paresse, la lâcheté et l’avarice, le poids de la dette et la domination des hommes.', ref:'صحيح البخاري' },
  { ar:'ٱللَّهُمَّ إِنِّيٓ أَسۡأَلُكَ ٱلۡهُدَىٰ وَٱلتُّقَىٰ وَٱلۡعَفَافَ وَٱلۡغِنَىٰ', en:'O Allah, I ask You for guidance, piety, chastity and self-sufficiency.', fr:'Ô Allah, je Te demande la guidée, la piété, la chasteté et la richesse intérieure.', ref:'صحيح مسلم' },
  { ar:'ٱللَّهُمَّ ٱغۡفِرۡ لِي وَٱرۡحَمۡنِي وَٱهۡدِنِي وَعَافِنِي وَٱرۡزُقۡنِي', en:'O Allah, forgive me, have mercy on me, guide me, grant me well-being and provide for me.', fr:'Ô Allah, pardonne-moi, fais-moi miséricorde, guide-moi, accorde-moi la santé et nourris-moi.', ref:'صحيح مسلم' },
  { ar:'ٱللَّهُمَّ ءَاتِ نَفۡسِي تَقۡوَىٰهَا وَزَكِّهَآ أَنتَ خَيۡرُ مَن زَكَّىٰهَا، أَنتَ وَلِيُّهَا وَمَوۡلَىٰهَا', en:'O Allah, grant my soul its piety and purify it; You are the best to purify it, You are its Guardian and Master.', fr:'Ô Allah, accorde à mon âme sa piété et purifie-la ; Tu es le meilleur à la purifier, Tu es son Maître et son Protecteur.', ref:'صحيح مسلم' },
  { ar:'رَبِّ ٱجۡعَلۡنِي مُقِيمَ ٱلصَّلَوٰةِ وَمِن ذُرِّيَّتِي', en:'My Lord, make me an establisher of prayer, and from my descendants as well.', fr:'Seigneur, fais que j’accomplisse la prière, ainsi que ma descendance.', ref:'إبراهيم ٤٠' },
  { ar:'رَبَّنَآ أَفۡرِغۡ عَلَيۡنَا صَبۡرٗا وَثَبِّتۡ أَقۡدَامَنَا', en:'Our Lord, pour upon us patience and make our footsteps firm.', fr:'Notre Seigneur, répands sur nous la patience et affermis nos pas.', ref:'البقرة ٢٥٠' },
  { ar:'ٱللَّهُمَّ إِنِّي ظَلَمۡتُ نَفۡسِي ظُلۡمٗا كَثِيرٗا، وَلَا يَغۡفِرُ ٱلذُّنُوبَ إِلَّآ أَنتَ، فَٱغۡفِرۡ لِي مَغۡفِرَةٗ مِّنۡ عِندِكَ وَٱرۡحَمۡنِيٓ', en:'O Allah, I have wronged myself greatly, and none forgives sins but You. So grant me forgiveness from Yourself and have mercy on me.', fr:'Ô Allah, je me suis fait beaucoup de tort à moi-même, et nul ne pardonne les péchés si ce n’est Toi. Accorde-moi donc un pardon de Ta part et fais-moi miséricorde.', ref:'البخاري ومسلم' },
  { ar:'ٱللَّهُمَّ بَارِكۡ لِي فِيمَا رَزَقۡتَنِي وَقِنِي عَذَابَ ٱلنَّارِ', en:'O Allah, bless for me what You have provided me and protect me from the punishment of the Fire.', fr:'Ô Allah, bénis pour moi ce que Tu m’as accordé et protège-moi du châtiment du Feu.', ref:'حسن' }
];

/* ───────────────── أسماء الله الحسنى ───────────────── */
const NAMES = [
  ['ٱللَّهُ','Allah','Allah'],['ٱلرَّحۡمَٰنُ','The Most Gracious','Le Tout Miséricordieux'],['ٱلرَّحِيمُ','The Most Merciful','Le Très Miséricordieux'],
  ['ٱلۡمَلِكُ','The King','Le Roi'],['ٱلۡقُدُّوسُ','The Most Holy','Le Très Pur'],['ٱلسَّلَٰمُ','The Source of Peace','La Paix'],
  ['ٱلۡمُؤۡمِنُ','The Guardian of Faith','Le Fidèle'],['ٱلۡمُهَيۡمِنُ','The Protector','Le Protecteur'],['ٱلۡعَزِيزُ','The Almighty','Le Tout-Puissant'],
  ['ٱلۡجَبَّارُ','The Compeller','Le Contraignant'],['ٱلۡمُتَكَبِّرُ','The Supreme','Le Suprême'],['ٱلۡخَالِقُ','The Creator','Le Créateur'],
  ['ٱلۡبَارِئُ','The Evolver','Le Créateur Novateur'],['ٱلۡمُصَوِّرُ','The Fashioner','Le Formateur'],['ٱلۡغَفَّارُ','The Constant Forgiver','Le Grand Pardonneur'],
  ['ٱلۡقَهَّارُ','The All-Prevailing','Le Dominateur'],['ٱلۡوَهَّابُ','The Supreme Bestower','Le Grand Donateur'],['ٱلرَّزَّاقُ','The Provider','Le Pourvoyeur'],
  ['ٱلۡفَتَّاحُ','The Supreme Solver','Le Grand Juge'],['ٱلۡعَلِيمُ','The All-Knowing','L’Omniscient'],['ٱلۡقَابِضُ','The Withholder','Celui qui retient'],
  ['ٱلۡبَاسِطُ','The Extender','Celui qui étend'],['ٱلۡخَافِضُ','The Reducer','Celui qui abaisse'],['ٱلرَّافِعُ','The Exalter','Celui qui élève'],
  ['ٱلۡمُعِزُّ','The Honourer','Celui qui honore'],['ٱلۡمُذِلُّ','The Dishonourer','Celui qui humilie'],['ٱلسَّمِيعُ','The All-Hearing','L’Audient'],
  ['ٱلۡبَصِيرُ','The All-Seeing','Le Clairvoyant'],['ٱلۡحَكَمُ','The Impartial Judge','Le Juge'],['ٱلۡعَدۡلُ','The Utterly Just','Le Juste'],
  ['ٱللَّطِيفُ','The Subtle One','Le Subtil'],['ٱلۡخَبِيرُ','The Acquainted','Le Bien-Informé'],['ٱلۡحَلِيمُ','The Most Forbearing','Le Très Patient'],
  ['ٱلۡعَظِيمُ','The Magnificent','L’Immense'],['ٱلۡغَفُورُ','The Great Forgiver','Le Pardonneur'],['ٱلشَّكُورُ','The Most Appreciative','Le Reconnaissant'],
  ['ٱلۡعَلِيُّ','The Most High','Le Très-Haut'],['ٱلۡكَبِيرُ','The Most Great','Le Très Grand'],['ٱلۡحَفِيظُ','The Preserver','Le Gardien'],
  ['ٱلۡمُقِيتُ','The Sustainer','Le Nourricier'],['ٱلۡحَسِيبُ','The Reckoner','Celui qui suffit'],['ٱلۡجَلِيلُ','The Majestic','Le Majestueux'],
  ['ٱلۡكَرِيمُ','The Most Generous','Le Généreux'],['ٱلرَّقِيبُ','The Watchful','L’Observateur'],['ٱلۡمُجِيبُ','The Responsive','Celui qui répond'],
  ['ٱلۡوَٰسِعُ','The All-Encompassing','Le Vaste'],['ٱلۡحَكِيمُ','The Most Wise','Le Sage'],['ٱلۡوَدُودُ','The Most Loving','Le Tout-Aimant'],
  ['ٱلۡمَجِيدُ','The Most Glorious','Le Glorieux'],['ٱلۡبَاعِثُ','The Resurrector','Le Résurrecteur'],['ٱلشَّهِيدُ','The All-Observing Witness','Le Témoin'],
  ['ٱلۡحَقُّ','The Absolute Truth','La Vérité'],['ٱلۡوَكِيلُ','The Trustee','Le Garant'],['ٱلۡقَوِيُّ','The All-Strong','Le Fort'],
  ['ٱلۡمَتِينُ','The Firm One','L’Inébranlable'],['ٱلۡوَلِيُّ','The Protecting Friend','Le Protecteur'],['ٱلۡحَمِيدُ','The All Praiseworthy','Le Digne de louange'],
  ['ٱلۡمُحۡصِي','The Accounter','Celui qui dénombre'],['ٱلۡمُبۡدِئُ','The Originator','L’Initiateur'],['ٱلۡمُعِيدُ','The Restorer','Celui qui fait revenir'],
  ['ٱلۡمُحۡيِ','The Giver of Life','Celui qui donne la vie'],['ٱلۡمُمِيتُ','The Bringer of Death','Celui qui donne la mort'],['ٱلۡحَيُّ','The Ever-Living','Le Vivant'],
  ['ٱلۡقَيُّومُ','The Self-Sustaining','L’Éternel Subsistant'],['ٱلۡوَٰجِدُ','The Finder','Celui qui trouve'],['ٱلۡمَاجِدُ','The Illustrious','Le Noble'],
  ['ٱلۡوَٰحِدُ','The One','L’Unique'],['ٱلصَّمَدُ','The Eternal Refuge','L’Absolu'],['ٱلۡقَادِرُ','The All-Powerful','Le Puissant'],
  ['ٱلۡمُقۡتَدِرُ','The Dominant','Le Tout-Puissant'],['ٱلۡمُقَدِّمُ','The Expediter','Celui qui avance'],['ٱلۡمُؤَخِّرُ','The Delayer','Celui qui retarde'],
  ['ٱلۡأَوَّلُ','The First','Le Premier'],['ٱلۡأٓخِرُ','The Last','Le Dernier'],['ٱلظَّٰهِرُ','The Manifest','L’Apparent'],
  ['ٱلۡبَاطِنُ','The Hidden','Le Caché'],['ٱلۡوَالِي','The Governor','Le Maître'],['ٱلۡمُتَعَالِي','The Self-Exalted','Le Transcendant'],
  ['ٱلۡبَرُّ','The Source of Goodness','Le Bienfaisant'],['ٱلتَّوَّابُ','The Accepter of Repentance','L’Accueillant au repentir'],['ٱلۡمُنۡتَقِمُ','The Avenger','Le Vengeur'],
  ['ٱلۡعَفُوُّ','The Pardoner','L’Indulgent'],['ٱلرَّءُوفُ','The Most Kind','Le Bienveillant'],['مَالِكُ ٱلۡمُلۡكِ','Master of the Dominion','Maître du Royaume'],
  ['ذُو ٱلۡجَلَٰلِ وَٱلۡإِكۡرَامِ','Lord of Majesty and Bounty','Détenteur de la Majesté et de la Générosité'],['ٱلۡمُقۡسِطُ','The Equitable','L’Équitable'],
  ['ٱلۡجَامِعُ','The Unifier','Le Rassembleur'],['ٱلۡغَنِيُّ','The Self-Sufficient','Le Riche'],['ٱلۡمُغۡنِي','The Enricher','Celui qui enrichit'],
  ['ٱلۡمَانِعُ','The Withholder','Celui qui empêche'],['ٱلضَّارُّ','The Distresser','Celui qui éprouve'],['ٱلنَّافِعُ','The Propitious','Celui qui est utile'],
  ['ٱلنُّورُ','The Light','La Lumière'],['ٱلۡهَادِي','The Guide','Le Guide'],['ٱلۡبَدِيعُ','The Incomparable Originator','L’Innovateur'],
  ['ٱلۡبَاقِي','The Everlasting','Le Permanent'],['ٱلۡوَٰرِثُ','The Inheritor','L’Héritier'],['ٱلرَّشِيدُ','The Guide to Truth','Le Bien-Guidé'],
  ['ٱلصَّبُورُ','The Most Patient','Le Très Patient']
];

/* ───────────────── القرّاء ───────────────── */
const RECITERS = [
  { ar:'مشاري العفاسي', en:'Mishary Alafasy', fr:'Mishary Alafasy' },
  { ar:'عبد الرحمن السديس', en:'Abdurrahman As-Sudais', fr:'Abdurrahman As-Sudais' },
  { ar:'سعود الشريم', en:'Saud Ash-Shuraim', fr:'Saud Ash-Shuraim' },
  { ar:'محمد صديق المنشاوي', en:'Mohamed Siddiq Al-Minshawi', fr:'Mohamed Siddiq Al-Minshawi' },
  { ar:'محمود خليل الحصري', en:'Mahmoud Khalil Al-Husary', fr:'Mahmoud Khalil Al-Husary' },
  { ar:'عبد الباسط عبد الصمد', en:'Abdul Basit Abdus Samad', fr:'Abdul Basit Abdus Samad' },
  { ar:'ماهر المعيقلي', en:'Maher Al Muaiqly', fr:'Maher Al Muaiqly' },
  { ar:'ياسر الدوسري', en:'Yasser Ad-Dussary', fr:'Yasser Ad-Dussary' },
  { ar:'إسلام صبحي', en:'Islam Sobhi', fr:'Islam Sobhi' },
  { ar:'ناصر القطامي', en:'Nasser Al Qatami', fr:'Nasser Al Qatami' }
];

/* ───────────────── عبادات المتتبع ───────────────── */
const HABITS = [
  { id:'fajr',   ar:'صلاة الفجر في وقتها', en:'Fajr on time', fr:'Fajr à l’heure', icon:'🌅' },
  { id:'five',   ar:'الصلوات الخمس',       en:'Five daily prayers', fr:'Cinq prières', icon:'🕌' },
  { id:'quran',  ar:'ورد قرآني',           en:'Quran portion', fr:'Part de Coran', icon:'📖' },
  { id:'morning',ar:'أذكار الصباح',        en:'Morning adhkar', fr:'Invocations du matin', icon:'☀️' },
  { id:'evening',ar:'أذكار المساء',        en:'Evening adhkar', fr:'Invocations du soir', icon:'🌆' },
  { id:'doha',   ar:'صلاة الضحى',          en:'Duha prayer', fr:'Prière de Doha', icon:'🌤️' },
  { id:'sadaqa', ar:'صدقة',                en:'Charity', fr:'Aumône', icon:'🤲' },
  { id:'fast',   ar:'صيام',                en:'Fasting', fr:'Jeûne', icon:'🌙' },
];

/* ───────────────── المناسبات الهجرية ───────────────── */
const EVENTS = [
  { m:1,  d:1,  ar:'رأس السنة الهجرية', en:'Islamic New Year', fr:'Nouvel an hégirien' },
  { m:1,  d:10, ar:'عاشوراء', en:'Ashura', fr:'Achoura' },
  { m:3,  d:12, ar:'المولد النبوي', en:"Prophet's Birthday", fr:'Naissance du Prophète' },
  { m:7,  d:27, ar:'الإسراء والمعراج', en:'Isra & Mi’raj', fr:'Isra et Mi’raj' },
  { m:9,  d:1,  ar:'بداية رمضان', en:'Start of Ramadan', fr:'Début du Ramadan' },
  { m:9,  d:27, ar:'ليلة القدر (ليلة ٢٧)', en:'Laylat al-Qadr', fr:'Nuit du Destin' },
  { m:10, d:1,  ar:'عيد الفطر', en:'Eid al-Fitr', fr:'Aïd al-Fitr' },
  { m:12, d:9,  ar:'يوم عرفة', en:'Day of Arafah', fr:'Jour de Arafat' },
  { m:12, d:10, ar:'عيد الأضحى', en:'Eid al-Adha', fr:'Aïd al-Adha' },
];
