import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

private static final String URL = "jdbc:sqlite:okul_projesi.db";



void main() {
    System.out.println("HAMDOLSUN");
    tabloOlustur();

    // 1. Önce birilerini ekleyelim (Test için)
    ogrenciEkle("Soner Hoca", 1923, 100.0);
    ogrenciEkle("Belinay Zehra", 2024, 95.5);

    // Mevcut listeyi gör
    ogrencileriGoster();

    // 2. Güncelleme yapalım: Soner Hoca'nın notunu 99 yapalım
    ogrenciGuncelle(1923, "Soner Hoca", 99.0);

    // 3. Silme yapalım: Belinay Zehra'yı silelim
    ogrenciSil(2024);

    // Son durumu gör
    ogrencileriGoster();
}//end main




public static Connection baglan() throws SQLException {
    try {
        // Sürücüyü zorla belleğe yükle
        Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
        System.err.println("Hata: SQLite JDBC kütüphanesi projenizde bulunamadı!");
        e.printStackTrace();
    }
    return DriverManager.getConnection(URL);
}//end baglan





public static void tabloOlustur() {
    String sql = "CREATE TABLE IF NOT EXISTS ogrenciler ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "ad_soyad TEXT NOT NULL,"
            + "numara INTEGER UNIQUE,"
            + "not_ortalamasi REAL"
            + ");";

    try (Connection conn = baglan();
         Statement stmt = conn.createStatement()) {

        stmt.execute(sql);
        System.out.println("Veritabanı hazır ve tablo oluşturuldu/kontrol edildi.");

    } catch (SQLException e) {
        System.err.println("Veritabanı hatası: " + e.getMessage());
    }
}//end tabloOlustur





public static void ogrenciEkle(String ad, int numara, double not) {
    String sql = "INSERT INTO ogrenciler(ad_soyad, numara, not_ortalamasi) VALUES(?,?,?)";

    try (Connection conn = baglan();
         var pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, ad);
        pstmt.setInt(2, numara);
        pstmt.setDouble(3, not);

        pstmt.executeUpdate();
        System.out.println("✅ Kayıt başarılı: " + ad);

    } catch (SQLException e) {
        System.err.println("❌ Ekleme hatası: " + e.getMessage());
    }
}//end ogrenciEkle






public static void ogrencileriGoster() {
    String sql = "SELECT * FROM ogrenciler";

    try (Connection conn = baglan();
         var stmt = conn.createStatement();
         var rs = stmt.executeQuery(sql)) {

        System.out.println("\n--- ÖĞRENCİ LİSTESİ ---");
        System.out.println("ID | Ad Soyad | Numara | Ort.");
        System.out.println("-------------------------------");

        while (rs.next()) {
            System.out.printf("%d | %s | %d | %.2f %n",
                    rs.getInt("id"),
                    rs.getString("ad_soyad"),
                    rs.getInt("numara"),
                    rs.getDouble("not_ortalamasi"));
        }

    } catch (SQLException e) {
        System.err.println("❌ Listeleme hatası: " + e.getMessage());
    }
}//ogrencileriGoster





public static void ogrenciGuncelle(int numara, String yeniAd, double yeniNot) {
    String sql = "UPDATE ogrenciler SET ad_soyad = ?, not_ortalamasi = ? WHERE numara = ?";

    try (Connection conn = baglan();
         var pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, yeniAd);
        pstmt.setDouble(2, yeniNot);
        pstmt.setInt(3, numara);

        int etkilenenSatir = pstmt.executeUpdate();
        if (etkilenenSatir > 0) {
            System.out.println("✅ " + numara + " numaralı öğrenci başarıyla güncellendi.");
        } else {
            System.out.println("⚠️ Güncellenecek öğrenci bulunamadı (No: " + numara + ")");
        }

    } catch (SQLException e) {
        System.err.println("❌ Güncelleme hatası: " + e.getMessage());
    }
}//end ogrenciGuncelle



public static void ogrenciSil(int numara) {
    String sql = "DELETE FROM ogrenciler WHERE numara = ?";

    try (Connection conn = baglan();
         var pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, numara);

        int etkilenenSatir = pstmt.executeUpdate();
        if (etkilenenSatir > 0) {
            System.out.println("🗑️ " + numara + " numaralı öğrenci silindi.");
        } else {
            System.out.println("⚠️ Silinecek öğrenci bulunamadı (No: " + numara + ")");
        }

    } catch (SQLException e) {
        System.err.println("❌ Silme hatası: " + e.getMessage());
    }
}//end ogrenciSil



public static void ogrenciAra(String arananIsim) {
    // Ismin içinde geçen harflere göre arar (örneğin "son" yazınca "Soner"i bulur)
    String sql = "SELECT * FROM ogrenciler WHERE ad_soyad LIKE ?";

    try (Connection conn = baglan();
         var pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, "%" + arananIsim + "%");
        var rs = pstmt.executeQuery();

        while (rs.next()) {
            System.out.println("Bulunan: " + rs.getString("ad_soyad"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}//end ogrenciAra






/*

2. İstatistiksel Sorgular (Aggregate Functions)
Veritabanındaki veriler üzerinden hesaplama yapmak için Java kodu yazmak yerine SQL'in gücünü kullanabiliriz.

Sınıf Ortalaması: SELECT AVG(not_ortalamasi) FROM ogrenciler

Öğrenci Sayısı: SELECT COUNT(*) FROM ogrenciler

En Yüksek Not: SELECT MAX(not_ortalamasi) FROM ogrenciler
 */


/*
3. Sıralama (Ordering)
Listeyi başarı sırasına (notu en yüksekten en düşüğe) veya alfabetik sıraya göre çekmek:

-- Notu en yüksek olan en üstte gelsin
SELECT * FROM ogrenciler ORDER BY not_ortalamasi DESC;

-- Alfabetik isim sırası
SELECT * FROM ogrenciler ORDER BY ad_soyad ASC;
 */


/*

4. İşlem Geçmişi veya Devamsızlık (Relational Tables)
Şu an tek tablonuz var. Gerçek bir projede "İlişkisel Veritabanı" yapısı kullanılır. Örneğin:

Tablo 1: ogrenciler (id, ad, numara)

Tablo 2: devamsizlik (id, ogrenci_id, tarih, durum)

Bu iki tabloyu JOIN komutu ile birleştirerek "Soner Hoca'nın hangi günler gelmediğini" tek bir sorguda çekebilirsiniz.
 */


/*

5. Veritabanı Yedekleme ve Transaction
Veri güvenliği için iki önemli kavram:

Backup: Veritabanı dosyasını (.db) başka bir klasöre kopyalayan küçük bir Java metodu.

Transaction (İşlem Grubu): Örneğin bir öğrenciden diğerine puan transferi yapıyorsunuz.
Birinden puan düşüp diğerine eklenmeden elektrik kesilirse veri bozulur.
conn.setAutoCommit(false) diyerek her iki işlem de başarılı olmadan veritabanına kaydetmemeyi sağlayabilirsiniz.
 */

/*
6. Parametreli Raporlar (Hız Sınırı)
"Notu 50'den düşük olan öğrencileri getir" gibi sorgular:
SELECT * FROM ogrenciler WHERE not_ortalamasi < 50;
 */