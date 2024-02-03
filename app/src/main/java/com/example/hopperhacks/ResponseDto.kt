import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("products")
    val products: List<Product>
)

data class Product(
    @SerializedName("barcode_number")
    val barcodeNumber: String,

    @SerializedName("barcode_formats")
    val barcodeFormats: String,

    @SerializedName("mpn")
    val mpn: String,

    @SerializedName("model")
    val model: String,

    @SerializedName("asin")
    val asin: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("manufacturer")
    val manufacturer: String,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("contributors")
    val contributors: List<Contributor>,

    @SerializedName("age_group")
    val ageGroup: String,

    @SerializedName("ingredients")
    val ingredients: String,

    @SerializedName("nutrition_facts")
    val nutritionFacts: String,

    @SerializedName("energy_efficiency_class")
    val energyEfficiencyClass: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("material")
    val material: String,

    @SerializedName("pattern")
    val pattern: String,

    @SerializedName("format")
    val format: String,

    @SerializedName("multipack")
    val multipack: String,

    @SerializedName("size")
    val size: String,

    @SerializedName("length")
    val length: String,

    @SerializedName("width")
    val width: String,

    @SerializedName("height")
    val height: String,

    @SerializedName("weight")
    val weight: String,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("features")
    val features: List<String>,

    @SerializedName("images")
    val images: List<String>,

    @SerializedName("last_update")
    val lastUpdate: String,

    @SerializedName("stores")
    val stores: List<Store>,

    @SerializedName("reviews")
    val reviews: List<Review>
)

data class Contributor(
    @SerializedName("role")
    val role: String,

    @SerializedName("name")
    val name: String
)

data class Store(
    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("currency_symbol")
    val currencySymbol: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("sale_price")
    val salePrice: String,

    @SerializedName("tax")
    val tax: List<Tax>,

    @SerializedName("link")
    val link: String,

    @SerializedName("item_group_id")
    val itemGroupId: String,

    @SerializedName("availability")
    val availability: String,

    @SerializedName("condition")
    val condition: String,

    @SerializedName("shipping")
    val shipping: List<Shipping>,

    @SerializedName("last_update")
    val lastUpdate: String
)

data class Tax(
    @SerializedName("country")
    val country: String,

    @SerializedName("region")
    val region: String,

    @SerializedName("rate")
    val rate: String,

    @SerializedName("tax_ship")
    val taxShip: String
)

data class Shipping(
    @SerializedName("country")
    val country: String,

    @SerializedName("region")
    val region: String,

    @SerializedName("service")
    val service: String,

    @SerializedName("price")
    val price: String
)

data class Review(
    @SerializedName("name")
    val name: String,

    @SerializedName("rating")
    val rating: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("review")
    val review: String,

    @SerializedName("date")
    val date: String
)
