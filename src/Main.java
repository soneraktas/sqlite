import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

private static final String URL = "jdbc:sqlite:okul_projesi.db";



void main() {
    System.out.println("HAMDOLSUN");
    tabloOlustur();

    // Birkaç veri ekleyelim
    ogrenciEkle("Soner Hoca", 1923, 100.0);
    ogrenciEkle("Belinay Zehra", 2024, 95.5);

    // Eklenenleri konsola yazdıralım
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