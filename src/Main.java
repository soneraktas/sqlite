import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

private static final String URL = "jdbc:sqlite:okul_projesi.db";

void main() {
    System.out.println("HAMDOLSUN");
    tabloOlustur();
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
}

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

