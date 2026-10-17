import '../../../core/utils/search_text.dart';
import '../domain/entities/place.dart';

/// قائمة المدن المبدئية — تُبنى مع التطبيق (أوفلاين أولًا).
///
/// الصيغة: `id|lat|lng|IANA timezone|ar|en|fr|cc`
///
/// ⚠️ **هذه بذرة (seed) قابلة للتوسعة** — ~٦٥ مدينة تغطّي العالم العربي
/// وأوروبا وأمريكا الشمالية وآسيا. التوسعة لاحقًا تكون بملف JSON مرفق
/// (راجع `docs/05` §4 أوفلاين أولًا).
abstract final class Cities {
  static const List<String> _rows = [
    // ─── مصر ───
    'cairo|30.0444|31.2357|Africa/Cairo|القاهرة|Cairo|Le Caire|EG',
    'giza|30.0131|31.2089|Africa/Cairo|الجيزة|Giza|Gizeh|EG',
    'alexandria|31.2001|29.9187|Africa/Cairo|الإسكندرية|Alexandria|Alexandrie|EG',
    'aswan|24.0889|32.8998|Africa/Cairo|أسوان|Aswan|Assouan|EG',
    'mansoura|31.0409|31.3785|Africa/Cairo|المنصورة|Mansoura|Mansourah|EG',
    'tanta|30.7865|31.0004|Africa/Cairo|طنطا|Tanta|Tanta|EG',
    // ─── السعودية والخليج ───
    'makkah|21.4225|39.8262|Asia/Riyadh|مكة المكرمة|Makkah|La Mecque|SA',
    'madinah|24.4686|39.6142|Asia/Riyadh|المدينة المنورة|Madinah|Médine|SA',
    'riyadh|24.7136|46.6753|Asia/Riyadh|الرياض|Riyadh|Riyad|SA',
    'jeddah|21.4858|39.1925|Asia/Riyadh|جدة|Jeddah|Djeddah|SA',
    'dammam|26.4207|50.0888|Asia/Riyadh|الدمام|Dammam|Dammam|SA',
    'dubai|25.2048|55.2708|Asia/Dubai|دبي|Dubai|Dubaï|AE',
    'abudhabi|24.4539|54.3773|Asia/Dubai|أبو ظبي|Abu Dhabi|Abou Dabi|AE',
    'doha|25.2854|51.5310|Asia/Qatar|الدوحة|Doha|Doha|QA',
    'kuwait|29.3759|47.9774|Asia/Kuwait|الكويت|Kuwait City|Koweït|KW',
    'manama|26.2285|50.5860|Asia/Bahrain|المنامة|Manama|Manama|BH',
    'muscat|23.5859|58.4059|Asia/Muscat|مسقط|Muscat|Mascate|OM',
    // ─── الشام والعراق ───
    'amman|31.9454|35.9284|Asia/Amman|عمّان|Amman|Amman|JO',
    'jerusalem|31.7683|35.2137|Asia/Jerusalem|القدس|Jerusalem|Jérusalem|PS',
    'beirut|33.8938|35.5018|Asia/Beirut|بيروت|Beirut|Beyrouth|LB',
    'damascus|33.5138|36.2765|Asia/Damascus|دمشق|Damascus|Damas|SY',
    'baghdad|33.3152|44.3661|Asia/Baghdad|بغداد|Baghdad|Bagdad|IQ',
    'erbil|36.1911|44.0092|Asia/Baghdad|أربيل|Erbil|Erbil|IQ',
    'sanaa|15.3694|44.1910|Asia/Aden|صنعاء|Sanaa|Sanaa|YE',
    // ─── شمال إفريقيا ───
    'khartoum|15.5007|32.5599|Africa/Khartoum|الخرطوم|Khartoum|Khartoum|SD',
    'tripoly|32.8872|13.1913|Africa/Tripoli|طرابلس|Tripoli|Tripoli|LY',
    'benghazi|32.1167|20.0667|Africa/Tripoli|بنغازي|Benghazi|Benghazi|LY',
    'tunis|36.8065|10.1815|Africa/Tunis|تونس|Tunis|Tunis|TN',
    'algiers|36.7538|3.0588|Africa/Algiers|الجزائر|Algiers|Alger|DZ',
    'oran|35.6991|-0.6359|Africa/Algiers|وهران|Oran|Oran|DZ',
    'casablanca|33.5731|-7.5898|Africa/Casablanca|الدار البيضاء|Casablanca|Casablanca|MA',
    'rabat|34.0209|-6.8416|Africa/Casablanca|الرباط|Rabat|Rabat|MA',
    'marrakesh|31.6295|-7.9811|Africa/Casablanca|مراكش|Marrakesh|Marrakech|MA',
    'tangier|35.7595|-5.8340|Africa/Casablanca|طنجة|Tangier|Tanger|MA',
    'nouakchott|18.0735|-15.9582|Africa/Nouakchott|نواكشوط|Nouakchott|Nouakchott|MR',
    // ─── أوروبا ───
    'paris|48.8566|2.3522|Europe/Paris|باريس|Paris|Paris|FR',
    'marseille|43.2965|5.3698|Europe/Paris|مارسيليا|Marseille|Marseille|FR',
    'lyon|45.7640|4.8357|Europe/Paris|ليون|Lyon|Lyon|FR',
    'toulouse|43.6047|1.4442|Europe/Paris|تولوز|Toulouse|Toulouse|FR',
    'lille|50.6292|3.0573|Europe/Paris|ليل|Lille|Lille|FR',
    'london|51.5074|-0.1278|Europe/London|لندن|London|Londres|GB',
    'manchester|53.4808|-2.2426|Europe/London|مانشستر|Manchester|Manchester|GB',
    'birmingham|52.4862|-1.8904|Europe/London|برمنغهام|Birmingham|Birmingham|GB',
    'berlin|52.5200|13.4050|Europe/Berlin|برلين|Berlin|Berlin|DE',
    'munich|48.1351|11.5820|Europe/Berlin|ميونخ|Munich|Munich|DE',
    'frankfurt|50.1109|8.6821|Europe/Berlin|فرانكفورت|Frankfurt|Francfort|DE',
    'brussels|50.8476|4.3572|Europe/Brussels|بروكسل|Brussels|Bruxelles|BE',
    'amsterdam|52.3676|4.9041|Europe/Amsterdam|أمستردام|Amsterdam|Amsterdam|NL',
    'madrid|40.4168|-3.7038|Europe/Madrid|مدريد|Madrid|Madrid|ES',
    'barcelona|41.3874|2.1686|Europe/Madrid|برشلونة|Barcelona|Barcelone|ES',
    'rome|41.9028|12.4964|Europe/Rome|روما|Rome|Rome|IT',
    'milan|45.4642|9.1900|Europe/Rome|ميلانو|Milan|Milan|IT',
    'stockholm|59.3293|18.0686|Europe/Stockholm|ستوكهولم|Stockholm|Stockholm|SE',
    'oslo|59.9139|10.7522|Europe/Oslo|أوسلو|Oslo|Oslo|NO',
    'copenhagen|55.6761|12.5683|Europe/Copenhagen|كوبنهاغن|Copenhagen|Copenhague|DK',
    'vienna|48.2082|16.3738|Europe/Vienna|فيينا|Vienna|Vienne|AT',
    'zurich|47.3769|8.5417|Europe/Zurich|زيورخ|Zurich|Zurich|CH',
    'istanbul|41.0082|28.9784|Europe/Istanbul|إسطنبول|Istanbul|Istanbul|TR',
    'ankara|39.9334|32.8597|Europe/Istanbul|أنقرة|Ankara|Ankara|TR',
    'sarajevo|43.8563|18.4131|Europe/Sarajevo|سراييفو|Sarajevo|Sarajevo|BA',
    'moscow|55.7558|37.6173|Europe/Moscow|موسكو|Moscow|Moscou|RU',
    // ─── آسيا ───
    'tehran|35.6892|51.3890|Asia/Tehran|طهران|Tehran|Téhéran|IR',
    'karachi|24.8607|67.0011|Asia/Karachi|كراتشي|Karachi|Karachi|PK',
    'lahore|31.5497|74.3436|Asia/Karachi|لاهور|Lahore|Lahore|PK',
    'islamabad|33.6844|73.0479|Asia/Karachi|إسلام آباد|Islamabad|Islamabad|PK',
    'dhaka|23.8103|90.4125|Asia/Dhaka|دكا|Dhaka|Dacca|BD',
    'delhi|28.6139|77.2090|Asia/Kolkata|نيودلهي|New Delhi|New Delhi|IN',
    'mumbai|19.0760|72.8777|Asia/Kolkata|مومباي|Mumbai|Bombay|IN',
    'jakarta|-6.2088|106.8456|Asia/Jakarta|جاكرتا|Jakarta|Jakarta|ID',
    'kualalumpur|3.1390|101.6869|Asia/Kuala_Lumpur|كوالالمبور|Kuala Lumpur|Kuala Lumpur|MY',
    'singapore|1.3521|103.8198|Asia/Singapore|سنغافورة|Singapore|Singapour|SG',
    // ─── إفريقيا جنوب الصحراء ───
    'lagos|6.5244|3.3792|Africa/Lagos|لاغوس|Lagos|Lagos|NG',
    'dakar|14.7167|-17.4677|Africa/Dakar|داكار|Dakar|Dakar|SN',
    'nairobi|-1.2921|36.8219|Africa/Nairobi|نيروبي|Nairobi|Nairobi|KE',
    // ─── أمريكا ───
    'newyork|40.7128|-74.0060|America/New_York|نيويورك|New York|New York|US',
    'chicago|41.8781|-87.6298|America/Chicago|شيكاغو|Chicago|Chicago|US',
    'houston|29.7604|-95.3698|America/Chicago|هيوستن|Houston|Houston|US',
    'losangeles|34.0522|-118.2437|America/Los_Angeles|لوس أنجلوس|Los Angeles|Los Angeles|US',
    'toronto|43.6532|-79.3832|America/Toronto|تورونتو|Toronto|Toronto|CA',
    'saopaulo|-23.5505|-46.6333|America/Sao_Paulo|ساو باولو|São Paulo|São Paulo|BR',
  ];

  /// كل المدن — تُبنى مرة واحدة عند أول استخدام.
  static final List<Place> all = _rows.map(_parseRow).toList(growable: false);

  static Place _parseRow(String row) {
    final parts = row.split('|');
    if (parts.length < 8) {
      throw StateError('صف مدينة غير صالح: $row');
    }
    return Place(
      id: parts[0],
      latitude: double.parse(parts[1]),
      longitude: double.parse(parts[2]),
      timeZoneId: parts[3],
      nameAr: parts[4],
      nameEn: parts[5],
      nameFr: parts[6],
      countryCode: parts[7],
    );
  }

  static Place? byId(String? id) {
    if (id == null) return null;
    for (final city in all) {
      if (city.id == id) return city;
    }
    return null;
  }

  /// البحث بالاسم — يطبّع العربية (القاهرة = القاهره) واللاتينية (Lyon = lyon).
  ///
  /// يبحث في كل اللغات الثلاث مرة واحدة: المستخدم يقدر يكتب «Cairo» أو «Le Caire».
  static List<Place> search(String query, {int limit = 40}) {
    final q = SearchText.normalize(query.trim());
    if (q.isEmpty) {
      return all.take(limit).toList();
    }
    final matches = <Place>[];
    for (final city in all) {
      if (SearchText.normalize(city.nameAr).contains(q) ||
          SearchText.normalize(city.nameEn).contains(q) ||
          SearchText.normalize(city.nameFr).contains(q)) {
        matches.add(city);
        if (matches.length >= limit) break;
      }
    }
    return matches;
  }

  /// أقرب مدينة لنقطة GPS — لتسمية الموقع الحالي بشكل مفهوم.
  static Place? nearestTo(GeoPoint point) {
    Place? best;
    double? bestKm;
    for (final city in all) {
      final km = point.distanceKmTo(city.point);
      if (bestKm == null || km < bestKm) {
        bestKm = km;
        best = city;
      }
    }
    return best;
  }
}
