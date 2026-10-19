/* ═══════════════════════════════════════════════════════════════
   i18n — عربي / English / Français
   ═══════════════════════════════════════════════════════════════ */
const T = {
  app_name:            {ar:'أثر', en:'Athar', fr:'Athar'},
  tagline:             {ar:'خلّي أثرك باقي', en:'Leave a trace of good', fr:'Laisse une trace de bien'},

  hi_morning:          {ar:'صباح النور', en:'Good morning', fr:'Bonjour'},
  hi_afternoon:        {ar:'يوم مبارك', en:'Good afternoon', fr:'Bon après-midi'},
  hi_evening:          {ar:'مساء الخير', en:'Good evening', fr:'Bonsoir'},
  hi_night:            {ar:'ليلة طيبة', en:'Good night', fr:'Bonne nuit'},

  tab_home:            {ar:'الرئيسية', en:'Home', fr:'Accueil'},
  tab_quran:           {ar:'القرآن', en:'Quran', fr:'Coran'},
  tab_adhkar:          {ar:'الأذكار', en:'Adhkar', fr:'Invocations'},
  tab_qibla:           {ar:'القبلة', en:'Qibla', fr:'Qibla'},
  tab_tasbih:          {ar:'السبحة', en:'Tasbih', fr:'Tasbih'},
  tab_more:            {ar:'المزيد', en:'More', fr:'Plus'},

  next_prayer:         {ar:'الصلاة القادمة', en:'Next prayer', fr:'Prochaine prière'},
  remaining:           {ar:'يتبقّى', en:'remaining', fr:'restant'},
  today:               {ar:'اليوم', en:'Today', fr:"Aujourd'hui"},
  now:                 {ar:'الآن', en:'Now', fr:'Maintenant'},
  madhab_label:        {ar:'المذهب', en:'Madhab', fr:'Madhab'},
  shafi:               {ar:'شافعي', en:'Shafi’i', fr:'Chaféite'},
  hanafi:              {ar:'حنفي', en:'Hanafi', fr:'Hanafite'},

  fajr:                {ar:'الفجر', en:'Fajr', fr:'Fajr'},
  sunrise:             {ar:'الشروق', en:'Sunrise', fr:'Lever'},
  dhuhr:               {ar:'الظهر', en:'Dhuhr', fr:'Dohr'},
  asr:                 {ar:'العصر', en:'Asr', fr:'Asr'},
  maghrib:             {ar:'المغرب', en:'Maghrib', fr:'Maghreb'},
  isha:                {ar:'العشاء', en:'Isha', fr:'Icha'},

  continue_reading:    {ar:'تابع القراءة', en:'Continue reading', fr:'Continuer la lecture'},
  ayah_no:             {ar:'آية', en:'Ayah', fr:'Verset'},
  wird_title:          {ar:'وردي اليوم', en:'My daily portion', fr:'Ma part du jour'},
  wird_sub:            {ar:'خطة ختمة ٣٠ يومًا', en:'30-day khatma plan', fr:'Plan de khatma sur 30 jours'},
  adhkar_today:        {ar:'أذكاري اليوم', en:'Today’s adhkar', fr:'Invocations du jour'},
  done:                {ar:'أكملت', en:'Completed', fr:'Terminé'},
  of:                  {ar:'من', en:'of', fr:'sur'},
  quick_actions:       {ar:'إجراءات سريعة', en:'Quick actions', fr:'Actions rapides'},
  verse_of_day:        {ar:'آية اليوم', en:'Verse of the day', fr:'Verset du jour'},

  qa_quran:            {ar:'القرآن', en:'Quran', fr:'Coran'},
  qa_adhkar:           {ar:'أذكار', en:'Adhkar', fr:'Invocations'},
  qa_qibla:            {ar:'القبلة', en:'Qibla', fr:'Qibla'},
  qa_tasbih:           {ar:'السبحة', en:'Tasbih', fr:'Tasbih'},
  qa_duas:             {ar:'أدعية', en:'Supplications', fr:'Douas'},
  qa_names:            {ar:'أسماء الله', en:'Names of Allah', fr:'Noms d’Allah'},

  prayer_times:        {ar:'المواقيت', en:'Prayer times', fr:'Horaires'},
  monthly_calendar:    {ar:'التقويم الشهري', en:'Monthly calendar', fr:'Calendrier mensuel'},
  calc_settings:       {ar:'إعدادات الحساب', en:'Calculation settings', fr:'Paramètres de calcul'},

  quran_index:         {ar:'فهرس السور', en:'Surah index', fr:'Index des sourates'},
  search_ph:           {ar:'ابحث في السور والآيات…', en:'Search surahs & verses…', fr:'Rechercher sourates & versets…'},
  filter_all:          {ar:'الكل', en:'All', fr:'Tous'},
  filter_meccan:       {ar:'مكية', en:'Meccan', fr:'Mecquoises'},
  filter_medinan:      {ar:'مدنية', en:'Medinan', fr:'Médinoises'},
  verses:              {ar:'آية', en:'verses', fr:'versets'},
  juz:                 {ar:'الجزء', en:'Juz', fr:'Juz'},
  meccan:              {ar:'مكية', en:'Meccan', fr:'Mecquoise'},
  medinan:             {ar:'مدنية', en:'Medinan', fr:'Médinoise'},

  reader_settings:     {ar:'إعدادات القراءة', en:'Reading settings', fr:'Paramètres de lecture'},
  font_size:           {ar:'حجم الخط', en:'Font size', fr:'Taille du texte'},
  translation:         {ar:'الترجمة', en:'Translation', fr:'Traduction'},
  tafsir:              {ar:'التفسير', en:'Tafsir', fr:'Tafsir'},
  reciter:             {ar:'القارئ', en:'Reciter', fr:'Récitateur'},
  none:                {ar:'بدون', en:'None', fr:'Aucune'},

  play_surah:          {ar:'استماع', en:'Listen', fr:'Écouter'},
  repeat:              {ar:'تكرار', en:'Repeat', fr:'Répéter'},
  speed:               {ar:'السرعة', en:'Speed', fr:'Vitesse'},
  download_offline:    {ar:'تحميل للاستماع أوفلاين', en:'Download for offline', fr:'Télécharger hors ligne'},
  save_mark:           {ar:'حفظ العلامة', en:'Save bookmark', fr:'Enregistrer'},

  bookmark:            {ar:'علامة', en:'Bookmark', fr:'Marque-page'},
  note:                {ar:'ملاحظة', en:'Note', fr:'Note'},
  color:               {ar:'تلوين', en:'Highlight', fr:'Surligner'},
  share:               {ar:'مشاركة', en:'Share', fr:'Partager'},
  copy:                {ar:'نسخ', en:'Copy', fr:'Copier'},
  saved_msg:           {ar:'تم الحفظ', en:'Saved', fr:'Enregistré'},

  adhkar_cat:          {ar:'تصنيفات الأذكار', en:'Adhkar categories', fr:'Catégories'},
  morning:             {ar:'أذكار الصباح', en:'Morning adhkar', fr:'Invocations du matin'},
  evening:             {ar:'أذكار المساء', en:'Evening adhkar', fr:'Invocations du soir'},
  after_prayer:        {ar:'بعد الصلاة', en:'After prayer', fr:'Après la prière'},
  sleep:               {ar:'أذكار النوم', en:'Before sleep', fr:'Avant le sommeil'},
  wake:                {ar:'الاستيقاظ', en:'Waking up', fr:'Au réveil'},
  food:                {ar:'الطعام', en:'Eating', fr:'Repas'},
  mosque:              {ar:'المسجد', en:'Mosque', fr:'Mosquée'},
  travel:              {ar:'السفر', en:'Travel', fr:'Voyage'},
  anxiety:             {ar:'الهمّ والحزن', en:'Anxiety & grief', fr:'Angoisse & tristesse'},
  rain:                {ar:'المطر', en:'Rain', fr:'Pluie'},

  count_of:            {ar:'عدد', en:'Count', fr:'Nombre'},
  tap_to_count:        {ar:'اضغط للتسبيح', en:'Tap to count', fr:'Appuyez pour compter'},
  complete_all:        {ar:'أكملت المجموعة 🎉', en:'Set completed 🎉', fr:'Série terminée 🎉'},
  reset:               {ar:'تصفير', en:'Reset', fr:'Réinitialiser'},

  dua_library:         {ar:'مكتبة الأدعية', en:'Supplications library', fr:'Bibliothèque de douas'},
  source:              {ar:'المصدر', en:'Source', fr:'Source'},

  names_title:         {ar:'أسماء الله الحسنى', en:'The 99 Names of Allah', fr:'Les 99 Noms d’Allah'},
  names_sub:           {ar:'اضغط على أي اسم لسماع النطق', en:'Tap a name to hear it', fr:'Touchez un nom pour l’écouter'},

  qibla_title:         {ar:'اتجاه القبلة', en:'Qibla direction', fr:'Direction de la Qibla'},
  qibla_hint:          {ar:'وجّه الهاتف وانتظر ثبات القراءة', en:'Point the phone and hold steady', fr:'Orientez le téléphone et attendez'},
  distance:            {ar:'المسافة إلى مكة', en:'Distance to Makkah', fr:'Distance vers La Mecque'},
  bearing:             {ar:'زاوية القبلة', en:'Qibla bearing', fr:'Azimut de la Qibla'},
  calibrate:           {ar:'حرّك الجهاز على شكل ∞ للمعايرة', en:'Move the device in a ∞ to calibrate', fr:'Bougez l’appareil en ∞ pour calibrer'},
  simulate:            {ar:'محاكاة دوران الجهاز (لا يوجد مستشعر)', en:'Simulate device rotation (no sensor)', fr:'Simuler la rotation (pas de capteur)'},
  how_without:         {ar:'كيف أعرف القبلة بدون بوصلة؟', en:'How to find Qibla without a compass?', fr:'Trouver la Qibla sans boussole ?'},

  tasbih_title:        {ar:'السبحة', en:'Tasbih', fr:'Tasbih'},
  target:              {ar:'الهدف', en:'Target', fr:'Objectif'},

  tracker_title:       {ar:'أثري', en:'My trace', fr:'Ma trace'},
  tracker_sub:         {ar:'متتبع العبادات اليومية', en:'Daily worship tracker', fr:'Suivi des adorations'},
  streak_days:         {ar:'يوم متصل', en:'day streak', fr:'jours consécutifs'},
  this_week:           {ar:'هذا الأسبوع', en:'This week', fr:'Cette semaine'},
  last_30:             {ar:'آخر ٣٠ يومًا', en:'Last 30 days', fr:'30 derniers jours'},
  habits:              {ar:'عباداتي', en:'My habits', fr:'Mes habitudes'},

  calendar_title:      {ar:'التاريخ والمناسبات', en:'Calendar & events', fr:'Calendrier & événements'},
  hijri:               {ar:'التاريخ الهجري', en:'Hijri date', fr:'Date hégirienne'},
  gregorian:           {ar:'التاريخ الميلادي', en:'Gregorian date', fr:'Date grégorienne'},
  upcoming:            {ar:'المناسبات القادمة', en:'Upcoming events', fr:'Événements à venir'},

  more_title:          {ar:'المزيد', en:'More', fr:'Plus'},
  bookmarks:           {ar:'المحفوظات', en:'Saved', fr:'Enregistrés'},
  settings:            {ar:'الإعدادات', en:'Settings', fr:'Paramètres'},
  about:               {ar:'عن أثر', en:'About Athar', fr:'À propos'},
  share_app:           {ar:'شارك التطبيق', en:'Share the app', fr:'Partager l’app'},
  rate_app:            {ar:'قيّم التطبيق', en:'Rate the app', fr:'Noter l’app'},

  settings_title:      {ar:'الإعدادات', en:'Settings', fr:'Paramètres'},
  general:             {ar:'عام', en:'General', fr:'Général'},
  language:            {ar:'اللغة', en:'Language', fr:'Langue'},
  theme:               {ar:'المظهر', en:'Theme', fr:'Thème'},
  text_size:           {ar:'حجم النص', en:'Text size', fr:'Taille du texte'},
  notifications:       {ar:'الإشعارات', en:'Notifications', fr:'Notifications'},
  adhan_sound:         {ar:'صوت الأذان', en:'Adhan sound', fr:'Son de l’adhan'},
  reminder_before:     {ar:'تذكير قبل الصلاة', en:'Reminder before prayer', fr:'Rappel avant la prière'},
  backup:              {ar:'نسخة احتياطية', en:'Backup', fr:'Sauvegarde'},
  sources:             {ar:'المصادر والتراخيص', en:'Sources & licenses', fr:'Sources & licences'},
  privacy:             {ar:'الخصوصية', en:'Privacy', fr:'Confidentialité'},

  theme_light:         {ar:'فاتح', en:'Light', fr:'Clair'},
  theme_dark:          {ar:'داكن', en:'Dark', fr:'Sombre'},
  theme_auto:          {ar:'تلقائي', en:'Auto', fr:'Auto'},
  size_small:          {ar:'صغير', en:'Small', fr:'Petit'},
  size_normal:         {ar:'متوسط', en:'Normal', fr:'Normal'},
  size_large:          {ar:'كبير', en:'Large', fr:'Grand'},
  size_xlarge:         {ar:'كبير جدًا', en:'Extra large', fr:'Très grand'},

  about_title:         {ar:'عن أثر', en:'About Athar', fr:'À propos d’Athar'},
  about_body:          {ar:'تطبيق إسلامي شامل، يعمل أوفلاين، بلا إعلانات ولا جمع بيانات. كل المحتوى موثّق المصدر، والمواقيت محسوبة فلكيًا محليًا على جهازك.', en:'An all-in-one Islamic app that works offline, with no ads and no data collection. Every piece of content is sourced, and prayer times are computed astronomically on your device.', fr:'Une application islamique complète, hors ligne, sans publicité ni collecte de données. Tout le contenu est sourcé, et les horaires sont calculés astronomiquement sur votre appareil.'},

  empty_bm_t:          {ar:'لا توجد علامات بعد', en:'No bookmarks yet', fr:'Aucun marque-page'},
  empty_bm_s:          {ar:'اضغط مطوّلًا على أي آية لحفظها هنا', en:'Long-press any verse to save it here', fr:'Appui long sur un verset pour l’enregistrer'},
  empty_note_t:        {ar:'لا توجد ملاحظات', en:'No notes yet', fr:'Aucune note'},

  save:                {ar:'حفظ', en:'Save', fr:'Enregistrer'},
  cancel:              {ar:'إلغاء', en:'Cancel', fr:'Annuler'},
  apply:               {ar:'تطبيق', en:'Apply', fr:'Appliquer'},
  on:                  {ar:'مفعّل', en:'On', fr:'Activé'},
  off:                 {ar:'معطّل', en:'Off', fr:'Désactivé'},
  coming_soon:         {ar:'قريبًا — في النسخة الكاملة', en:'Coming soon in the full app', fr:'Bientôt dans l’app complète'},
  demo_data:           {ar:'بيانات تجريبية', en:'Demo data', fr:'Données de démo'},
};

let LANG = 'ar';
function t(key, fallback){
  const e = T[key];
  if(!e) return fallback !== undefined ? fallback : key;
  return e[LANG] || e.ar || key;
}
