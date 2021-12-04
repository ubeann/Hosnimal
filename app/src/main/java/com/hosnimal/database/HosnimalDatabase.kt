package com.hosnimal.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hosnimal.database.dao.ProductDao
import com.hosnimal.database.dao.UserDao
import com.hosnimal.model.Product
import com.hosnimal.model.User
import java.util.concurrent.Executors

@Database(version = 1, entities = [User::class, Product::class], exportSchema = true)
@TypeConverters(Converters::class)
abstract class HosnimalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE : HosnimalDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HosnimalDatabase {
            if (INSTANCE == null) {
                synchronized(HosnimalDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HosnimalDatabase::class.java, "hosnimal_database.db")
                        .addCallback(prePopulate)
                        .build()
                }
            }
            return INSTANCE as HosnimalDatabase
        }

        // Callback for pre-populated database (products and hospitals)
        private val prePopulate = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    INSTANCE?.productDao()?.insert(*products)
                }
            }
        }

        // Pre-populated products
        private val products = arrayOf(
            Product(
                1,
                "COLIBACT 100 gr Serbuk",
                "TEST",
                "Antibiotik sulfa trimetroprim obat hewan dengan formula khusus COLIBACT sangat efektif terhadap bakteri Gram-positif dan Gram-negatif seperti: Staphylococcus spp., Streptococcus spp., Bacillus anthrads, E. coli, Corynebacterium pyogenes, Haemophilus spp., Pasteurella spp., Klebsiella spp., Salmonella spp., Bordetella bronchiseptica, dll.",
                43000,
                57,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                2,
                "Intertrim LA",
                "TEST",
                "Mengobati infeksi saluran pernafasan, pencernaan dan perkencingan. Kombinasi Sulfadoxine dan Trimethoprim bekerja secara sinergis potensiasi, bersifat bakterisidal terhadap bakteri gram negatif dan gram positif seperti E. coli, Haemophilus, Pasteurella, Salmonella, Staphylococcus, Streptococcus spp. dan parasit darah seperti Leucocytozoon spp. Sulfadoxine dan Trimethoprim menghambat sintesis asam folat bakteri dan parasit darah dengan cara yang berbeda sehingga menghasilkan hambatan ganda.",
                115000,
                34,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                3,
                "MEDOXY LA 100 ml",
                "TEST",
                "MEDOXY-LA adalah obat suntik yang mengandung Oxytetracycline yang bekerja menghambat sintesis protein bakteri, mempunyai spektrum kerja yang luas dan efektif terhadap sebagian besar bakteri Gram (-) dan Gram (+) termasuk yang telah resisten terhadap Penicillin.",
                98800,
                42,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                4,
                "AMINOVIT serbuk 250 gram",
                "TEST",
                "Aminovit Serbuk larut air mengandung vitamin, asam amino dan elektrolit dengan komposisi yang seimbang. Berfungsi meningkatkan produksi dan kualitas telur. AMINOVIT merupakan sediaan berbentuk serbuk larut air berwarna coklat muda dengan kandungan 11 vitamin, 2 asam amino dan 4 elektrolit untuk ayam petelur.",
                45450,
                29,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                5,
                "Drontal Dog",
                "TEST",
                "Obat cacing untuk anjing semua ras per tablet. Obat cacing untuk anjing beraroma daging sapi. Dapat diberikan langsung maupun dicampur dengan makanan. Untuk pembasmian rutin diberikan setiap 3 bulan sekali, untuk infeksi cacing gelang berat,bisa diulang setelah 14 hari. Drontal Plus Flavour Dog membasmi ascarida, cacing tambang, cacing cambuk, cacing pita, giardia.",
                19000,
                72,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                6,
                "Penstrep 45",
                "TEST",
                "Tiap gram mengandung procaine penicilin g : 400.000 iu, streptomycine, sulfate : 500mg. efektif mengobati mastitis yang di sebabkan streptococci, staphylococci, clostridium tetani infeksi, abses bacterial, erysiphelas infeksi pada babi, dan pneumonia infeksi bakterial.",
                50000,
                62,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                7,
                "Flumax Vetplus 9 ml",
                "TEST",
                "Suplemen nutrisi lezat yang mengandung kombinasi unik asam amino, ekstrak tumbuhan, vitamin dan mineral untuk membantu mendukung kesehatan saluran pernapasan pada kucing. Diformulasikan secara khusus untuk memberi kucing Anda nutrisi yang mereka butuhkan untuk gaya hidup sehat dan bahagia. Flumax membantu mengobati masalah pernafasan dan mengurangi resiko pernafasan pada kucing dan menghindari flu, hidung atau mata berair, nafsu makan berkurang, batuk atau bersin-bersin.",
                29000,
                7,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                8,
                "Albusmin 10 Kapsul",
                "TEST",
                "Albusmin, ekstrak ikan gabus yang bisa membantu mempercepat masa pemulihan pasca operasi pada hewan kesayangan. Kandungan albumino yang terdapat dalam albusmin sudah melalui penelitian dapat membantu proses pemulihan lebih cepat seperti pasca operasi mayor atau minor.Dapat juga mendukung proses pemulihan kucing dengan kasus FLUTD kencing darah akibat kristal-kristal yang mengiritasi dinding saluran kemih, memulihkan stamina dan daya tahan tubuh hewan peliharaan.",
                64000,
                17,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                9,
                "Kicau mas probiotik tetes 3 ml",
                "TEST",
                "Probiotik super untuk Pemicu bunyiburung, membuat burung tidak stress, membuat burung bersuara emas dan melengking tinggi, mengurangi macet bunyi, diformulasikan probiotik khusus burung dengan formulasi terbaik untuk burung. Komposisi : Bacillus subtilis, Lactobacillus, Saccharomyces, Aspergillus, Rhodopseudomonas, Actinomycetes, Nitrobacter.",
                45000,
                55,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                10,
                "HEMADEX 100 ml",
                "TEST",
                "HEMADEX merupakan produk yang mengandung zat besi dalam bentuk injeksi yang diperlukan untuk membentuk haemoglobin dan enzim. Untuk pengobatan terhadap anemia karena defisiensi zat besi yang diakibatkan oleh parasit, penyakit infeksi dan keadaan gizi yang tidak seimbang pada anak babi, sapi, kambing, domba, anjing dan kucing, membantu pertumbuhan dan pertambahan berat badan, dan meningkatkan daya tahan tubuh terhadap penyakit.",
                78800,
                71,
                listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            )
        )
    }
}