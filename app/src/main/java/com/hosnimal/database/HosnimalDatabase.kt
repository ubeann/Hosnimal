package com.hosnimal.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hosnimal.database.dao.HospitalDao
import com.hosnimal.database.dao.OrderDao
import com.hosnimal.database.dao.ProductDao
import com.hosnimal.database.dao.UserDao
import com.hosnimal.model.Hospital
import com.hosnimal.model.Order
import com.hosnimal.model.Product
import com.hosnimal.model.User
import java.time.OffsetTime
import java.time.ZoneOffset
import java.util.concurrent.Executors

@Database(
    version = 1,
    entities = [
        User::class,
        Product::class,
        Order::class,
        Hospital::class
   ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HosnimalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun hospitalDao(): HospitalDao

    companion object {
        @Volatile
        private var INSTANCE : HosnimalDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HosnimalDatabase {
            if (INSTANCE == null) {
                synchronized(HosnimalDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HosnimalDatabase::class.java, "hosnimal_database.db")
                        .fallbackToDestructiveMigration()
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
                    INSTANCE?.hospitalDao()?.insert(*hospitals)
                }
            }
        }

        // Pre-populated products
        private val products = arrayOf(
            Product(
                id = 1,
                name = "COLIBACT 100 gr Serbuk",
                category = "TEST",
                description = "Antibiotik sulfa trimetroprim obat hewan dengan formula khusus COLIBACT sangat efektif terhadap bakteri Gram-positif dan Gram-negatif seperti: Staphylococcus spp., Streptococcus spp., Bacillus anthrads, E. coli, Corynebacterium pyogenes, Haemophilus spp., Pasteurella spp., Klebsiella spp., Salmonella spp., Bordetella bronchiseptica, dll.",
                price = 43000,
                stock = 57,
                images =  listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 2,
                name = "Intertrim LA",
                category = "TEST",
                description = "Mengobati infeksi saluran pernafasan, pencernaan dan perkencingan. Kombinasi Sulfadoxine dan Trimethoprim bekerja secara sinergis potensiasi, bersifat bakterisidal terhadap bakteri gram negatif dan gram positif seperti E. coli, Haemophilus, Pasteurella, Salmonella, Staphylococcus, Streptococcus spp. dan parasit darah seperti Leucocytozoon spp. Sulfadoxine dan Trimethoprim menghambat sintesis asam folat bakteri dan parasit darah dengan cara yang berbeda sehingga menghasilkan hambatan ganda.",
                price = 115000,
                stock = 34,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 3,
                name = "MEDOXY LA 100 ml",
                category = "TEST",
                description = "MEDOXY-LA adalah obat suntik yang mengandung Oxytetracycline yang bekerja menghambat sintesis protein bakteri, mempunyai spektrum kerja yang luas dan efektif terhadap sebagian besar bakteri Gram (-) dan Gram (+) termasuk yang telah resisten terhadap Penicillin.",
                price = 98800,
                stock = 42,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 4,
                name = "AMINOVIT serbuk 250 gram",
                category = "TEST",
                description = "Aminovit Serbuk larut air mengandung vitamin, asam amino dan elektrolit dengan komposisi yang seimbang. Berfungsi meningkatkan produksi dan kualitas telur. AMINOVIT merupakan sediaan berbentuk serbuk larut air berwarna coklat muda dengan kandungan 11 vitamin, 2 asam amino dan 4 elektrolit untuk ayam petelur.",
                price = 45450,
                stock = 29,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 5,
                name = "Drontal Dog",
                category = "TEST",
                description = "Obat cacing untuk anjing semua ras per tablet. Obat cacing untuk anjing beraroma daging sapi. Dapat diberikan langsung maupun dicampur dengan makanan. Untuk pembasmian rutin diberikan setiap 3 bulan sekali, untuk infeksi cacing gelang berat,bisa diulang setelah 14 hari. Drontal Plus Flavour Dog membasmi ascarida, cacing tambang, cacing cambuk, cacing pita, giardia.",
                price = 19000,
                stock = 72,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 6,
                name = "Penstrep 45",
                category = "TEST",
                description = "Tiap gram mengandung procaine penicilin g : 400.000 iu, streptomycine, sulfate : 500mg. efektif mengobati mastitis yang di sebabkan streptococci, staphylococci, clostridium tetani infeksi, abses bacterial, erysiphelas infeksi pada babi, dan pneumonia infeksi bakterial.",
                price = 50000,
                stock = 62,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 7,
                name = "Flumax Vetplus 9 ml",
                category = "TEST",
                description = "Suplemen nutrisi lezat yang mengandung kombinasi unik asam amino, ekstrak tumbuhan, vitamin dan mineral untuk membantu mendukung kesehatan saluran pernapasan pada kucing. Diformulasikan secara khusus untuk memberi kucing Anda nutrisi yang mereka butuhkan untuk gaya hidup sehat dan bahagia. Flumax membantu mengobati masalah pernafasan dan mengurangi resiko pernafasan pada kucing dan menghindari flu, hidung atau mata berair, nafsu makan berkurang, batuk atau bersin-bersin.",
                price = 29000,
                stock = 7,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 8,
                name = "Albusmin 10 Kapsul",
                category = "TEST",
                description = "Albusmin, ekstrak ikan gabus yang bisa membantu mempercepat masa pemulihan pasca operasi pada hewan kesayangan. Kandungan albumino yang terdapat dalam albusmin sudah melalui penelitian dapat membantu proses pemulihan lebih cepat seperti pasca operasi mayor atau minor.Dapat juga mendukung proses pemulihan kucing dengan kasus FLUTD kencing darah akibat kristal-kristal yang mengiritasi dinding saluran kemih, memulihkan stamina dan daya tahan tubuh hewan peliharaan.",
                price = 64000,
                stock = 17,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 9,
                name = "Kicau mas probiotik tetes 3 ml",
                category = "TEST",
                description = "Probiotik super untuk Pemicu bunyiburung, membuat burung tidak stress, membuat burung bersuara emas dan melengking tinggi, mengurangi macet bunyi, diformulasikan probiotik khusus burung dengan formulasi terbaik untuk burung. Komposisi : Bacillus subtilis, Lactobacillus, Saccharomyces, Aspergillus, Rhodopseudomonas, Actinomycetes, Nitrobacter.",
                price = 45000,
                stock = 55,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            ),
            Product(
                id = 10,
                name = "HEMADEX 100 ml",
                category = "TEST",
                description = "HEMADEX merupakan produk yang mengandung zat besi dalam bentuk injeksi yang diperlukan untuk membentuk haemoglobin dan enzim. Untuk pengobatan terhadap anemia karena defisiensi zat besi yang diakibatkan oleh parasit, penyakit infeksi dan keadaan gizi yang tidak seimbang pada anak babi, sapi, kambing, domba, anjing dan kucing, membantu pertumbuhan dan pertambahan berat badan, dan meningkatkan daya tahan tubuh terhadap penyakit.",
                price = 78800,
                stock = 71,
                images = listOf("https://assets.pikiran-rakyat.com/crop/0x155:1920x1235/x/photo/2021/08/01/658666320.jpg")
            )
        )

        // Pre-populated hospitals
        private val zoneOffset = ZoneOffset.ofHours(7)
        private val hospitals = arrayOf(
            Hospital(
                id = 1,
                name = "Rumah Sakit Hewan Pendidikan Universitas Airlangga",
                open = OffsetTime.of(9,0,0,0, zoneOffset),
                close = OffsetTime.of(17,0,0,0, zoneOffset),
                location = "Kampus C Universitas Airlangga, Mulyorejo, Kec. Mulyorejo, Kota SBY, Jawa Timur 60115",
                x = -7.266648939479121,
                y = 112.78352390110503,
                images = listOf(
                    "https://drive.google.com/uc?id=1HGpkylT8NxjKGgzvjnMHyQpyDcQc4SDm",
                    "https://drive.google.com/uc?id=1EZIEy8cN4-Gxi9J0alsQhBjZPU9ziaev",
                    "https://drive.google.com/uc?id=1EtiYOedWbytcTkdDRyAVuJwESm0Z8J7p"
                )
            ),
            Hospital(
                id = 2,
                name = "Rumah Sakit Hewan Disnak Provinsi Jawa Timur",
                open = OffsetTime.of(7,0,0,0, zoneOffset),
                close = OffsetTime.of(15,30,0,0, zoneOffset),
                location = "JL. Jend. A. Yani No. 202, Surabaya, 60253",
                x = -7.331851,
                y = 112.7276703,
                images = listOf(
                    "https://drive.google.com/uc?id=1VUm4BJEDifYuCZZ_oDzlDvpqV0iPatVt",
                    "https://drive.google.com/uc?id=1_iIYo5tmFSXjnvoCcnTP0nmq0QUGqVh7",
                    "https://drive.google.com/uc?id=1PXFCbQ8k_Zzo-kyww2cQaTRd0Vwq3xgl"
                )
            ),
            Hospital(
                id = 3,
                name = "Surabaya Animal Clinic",
                open = OffsetTime.of(9,0,0,0, zoneOffset),
                close = OffsetTime.of(17,0,0,0, zoneOffset),
                location = "Perumahan YKP Pandugo II Blok E-6, Jl. Pandugo Timur XIII No.Kel, Penjaringan Sari, Kec. Rungkut, Kota SBY, 60297",
                instagram = "https://www.instagram.com/surabaya_animalclinic/",
                x = -7.3199045,
                y = 112.7868736,
                images = listOf(
                    "https://drive.google.com/uc?id=1HP-IZ6K7lUf8WSEdYXckcB0b6oSABB13",
                    "https://drive.google.com/uc?id=18wv9hKMvLf-rVIFNe7LUMu7_ktaWJbqH"
                )
            ),
            Hospital(
                id = 4,
                name = "Lingkar Satwa Pet Clinic",
                open = OffsetTime.of(9,0,0,0, zoneOffset),
                close = OffsetTime.of(21,0,0,0, zoneOffset),
                location = "Jl. Sumatera No.31L, Gubeng, Kec. Gubeng, Kota SBY, 60281",
                instagram = "https://www.instagram.com/lisapetclinic.surabaya/",
                x = -7.3199044,
                y = 112.7803075,
                images = listOf(
                    "https://drive.google.com/uc?id=16o9jp50tWvaxTFsoVrCVRsVmX1f-VzUG",
                    "https://drive.google.com/uc?id=1cHt1wgC1kCSH3EUVncTONxAJkNquLLyx"
                )
            ),
            Hospital(
                id = 5,
                name = "Jeje Pet Shop & Dokter Hewan",
                open = OffsetTime.of(8,0,0,0, zoneOffset),
                close = OffsetTime.of(15,0,0,0, zoneOffset),
                location = "Jl. Pondok Maritim Indah Cluster Boulevard No.2 Balas Klumprik, Kec. Wiyung, Kota SBY, 60222",
                instagram = "https://www.instagram.com/jejepetshop/",
                x = -7.3274161,
                y = 112.6880759,
                images = listOf(
                    "https://drive.google.com/uc?id=1Xb55kl2qn5YVVTj2Iq-nrOO4adK8tpgB",
                    "https://drive.google.com/uc?id=1zlIddeWA7MrM1gnaLJMnjDgX60a7xrgY",
                    "https://drive.google.com/uc?id=1ySUMxgC03HSjF371VicPH1m8PIvO6hYZ",
                    "https://drive.google.com/uc?id=1d9yAfvXIiKBez8Jq7pghBr7Yvcv6jWko"
                )
            ),
            Hospital(
                id = 6,
                name = "Klinik Hewan Habitat Satwa",
                open = OffsetTime.of(8,0,0,0, zoneOffset),
                close = OffsetTime.of(20,0,0,0, zoneOffset),
                location = "Jl. Bratang Gede III C No.2A, Ngagelrejo, Kec. Wonokromo, Kota SBY, 60245",
                instagram = "https://www.instagram.com/habitatsatwa/",
                x = -7.2990437,
                y = 112.751622,
                images = listOf(
                    "https://drive.google.com/uc?id=11ARWHFCrPO1Jq-XMFxyoIbP6qEykt3b-",
                    "https://drive.google.com/uc?id=118TtzvJU6zX0F96fTZO_vWvVFELsBbHW",
                    "https://drive.google.com/uc?id=15mNuHGGzi6D69NFvuN4Q-lym-lo4zSpo"
                )
            ),
            Hospital(
                id = 7,
                name = "CaroVet Dokter Hewan",
                open = OffsetTime.of(0,0,10,0, zoneOffset),
                close = OffsetTime.of(23,59,0,0, zoneOffset),
                location = "Jl. Raya Wiyung No.9, Kec. Wiyung, Kota SBY, 60228",
                instagram = "https://www.instagram.com/carovet_id/",
                x = -7.3119968,
                y = 112.6868929,
                images = listOf(
                    "https://drive.google.com/uc?id=1R7nK4qVyayULbC6KHVRqUjSqBadoLn3z",
                    "https://drive.google.com/uc?id=1kaT5f5k2XjZW7ADulp8w9PyQzyhmO2TR",
                    "https://drive.google.com/uc?id=1afaEFdpOR1QxqEtS_TJfHziWW9JAA7Zp"
                )
            ),
            Hospital(
                id = 8,
                name = "Harmoni Pet Care - Klinik Hewan Surabaya",
                open = OffsetTime.of(0,0,0,0, zoneOffset),
                close = OffsetTime.of(0,0,10,0, zoneOffset),
                location = "Jl. Gayungsari Timur III Blok MGJ No.3, Menanggal, Gayungan, Kota SBY, 60234",
                instagram = "https://www.instagram.com/harmonipetcare/",
                x = -7.3369348,
                y = 112.7187758,
                images = listOf(
                    "https://drive.google.com/uc?id=1XuYwwn3NphMXbZL6-EeyAX8tWXWGvZvq",
                    "https://drive.google.com/uc?id=1Bt3HqtF_5__aVCCtT89VVJUD8eYYPEmw",
                    "https://drive.google.com/uc?id=1AftGRlukPbEvHTDdoUa7TbH4cic_SNWZ"
                )
            )
        )
    }
}