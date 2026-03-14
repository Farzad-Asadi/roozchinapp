package com.example.compoundeffectV1_01.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Boy
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.Desk
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Fence
import androidx.compose.material.icons.filled.FireExtinguisher
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Outlet
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Villa
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.Window
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.reminder.ReminderMode
import com.example.compoundeffectV1_01.data.dataBaseRoom.tables.taskSchedule.ScheduleMode
import androidx.compose.material.icons.filled.*

data class IconOption(val key: String, val icon: ImageVector)
data class IconSection(val title: String, val options: List<IconOption>)





fun buildIconSections(): List<IconSection> = listOf(

    IconSection(
        title = "Buildings & furniture",
        options = listOf(
            IconOption("Home", Icons.Filled.Home),
            IconOption("Apartment", Icons.Filled.Apartment),
            IconOption("Business", Icons.Filled.Business),
            IconOption("Warehouse", Icons.Filled.Warehouse),
            IconOption("Store", Icons.Filled.Store),
            IconOption("Storefront", Icons.Filled.Storefront),
            IconOption("Church", Icons.Filled.Church),
            IconOption("Mosque", Icons.Filled.Mosque),
            IconOption("Synagogue", Icons.Filled.Synagogue),
            IconOption("TempleBuddhist", Icons.Filled.TempleBuddhist),
            IconOption("TempleHindu", Icons.Filled.TempleHindu),
            IconOption("Castle", Icons.Filled.Castle),
            IconOption("Stadium", Icons.Filled.Stadium),
            IconOption("MeetingRoom", Icons.Filled.MeetingRoom),
            IconOption("DoorFront", Icons.Filled.DoorFront),
            IconOption("Chair", Icons.Filled.Chair),
            IconOption("Weekend", Icons.Filled.Weekend),
            IconOption("Desk", Icons.Filled.Desk),
            IconOption("Kitchen", Icons.Filled.Kitchen),
            IconOption("Bathtub", Icons.Filled.Bathtub),
            IconOption("Bathroom", Icons.Filled.Bathroom),
            IconOption("Key", Icons.Filled.Key),
            IconOption("Lock", Icons.Filled.Lock),
        )
    ),

    IconSection(
        title = "People",
        options = listOf(
            IconOption("Person", Icons.Filled.Person),
            IconOption("Groups", Icons.Filled.Groups),
            IconOption("FamilyRestroom", Icons.Filled.FamilyRestroom),
            IconOption("ChildCare", Icons.Filled.ChildCare),
            IconOption("AccessibilityNew", Icons.Filled.AccessibilityNew),
            IconOption("Elderly", Icons.Filled.Elderly),
            IconOption("Handshake", Icons.Filled.Handshake),
            IconOption("ThumbUp", Icons.Filled.ThumbUp),
            IconOption("School", Icons.Filled.School),
            IconOption("Engineering", Icons.Filled.Engineering),
            IconOption("Work", Icons.Filled.Work),
            IconOption("Badge", Icons.Filled.Badge),
        )
    ),

    IconSection(
        title = "Food & cooking",
        options = listOf(
            IconOption("Restaurant", Icons.Filled.Restaurant),
            IconOption("LocalCafe", Icons.Filled.LocalCafe),
            IconOption("LunchDining", Icons.Filled.LunchDining),
            IconOption("BakeryDining", Icons.Filled.BakeryDining),
            IconOption("Fastfood", Icons.Filled.Fastfood),
            IconOption("LocalPizza", Icons.Filled.LocalPizza),
            IconOption("Icecream", Icons.Filled.Icecream),
            IconOption("Cake", Icons.Filled.Cake),
            IconOption("LocalBar", Icons.Filled.LocalBar),
            IconOption("WineBar", Icons.Filled.WineBar),
            IconOption("Coffee", Icons.Filled.Coffee),
            IconOption("KebabDining", Icons.Filled.KebabDining),
            IconOption("RamenDining", Icons.Filled.RamenDining),
            IconOption("SoupKitchen", Icons.Filled.SoupKitchen),
            IconOption("DinnerDining", Icons.Filled.DinnerDining),
            IconOption("SetMeal", Icons.Filled.SetMeal),
            IconOption("Blender", Icons.Filled.Blender),
            IconOption("Microwave", Icons.Filled.Microwave),
            IconOption("OutdoorGrill", Icons.Filled.OutdoorGrill),
            IconOption("Kitchen", Icons.Filled.Kitchen),
        )
    ),

    IconSection(
        title = "Shopping & clothes",
        options = listOf(
            IconOption("ShoppingCart", Icons.Filled.ShoppingCart),
            IconOption("LocalMall", Icons.Filled.LocalMall),
            IconOption("Sell", Icons.Filled.Sell),
            IconOption("ReceiptLong", Icons.Filled.ReceiptLong),
            IconOption("LocalOffer", Icons.Filled.LocalOffer),
            IconOption("Checkroom", Icons.Filled.Checkroom),
            IconOption("Diamond", Icons.Filled.Diamond),
            IconOption("Watch", Icons.Filled.Watch),
            IconOption("Luggage", Icons.Filled.Luggage),
            IconOption("CardGiftcard", Icons.Filled.CardGiftcard),
            IconOption("Celebration", Icons.Filled.Celebration),
            IconOption("Loyalty", Icons.Filled.Loyalty),
            IconOption("Payments", Icons.Filled.Payments),
        )
    ),

    IconSection(
        title = "Sport",
        options = listOf(
            IconOption("SportsFootball", Icons.Filled.SportsFootball),
            IconOption("SportsSoccer", Icons.Filled.SportsSoccer),
            IconOption("SportsBasketball", Icons.Filled.SportsBasketball),
            IconOption("SportsVolleyball", Icons.Filled.SportsVolleyball),
            IconOption("SportsTennis", Icons.Filled.SportsTennis),
            IconOption("SportsBaseball", Icons.Filled.SportsBaseball),
            IconOption("SportsCricket", Icons.Filled.SportsCricket),
            IconOption("SportsGolf", Icons.Filled.SportsGolf),
            IconOption("SportsHockey", Icons.Filled.SportsHockey),
            IconOption("SportsRugby", Icons.Filled.SportsRugby),
            IconOption("SportsMma", Icons.Filled.SportsMma),
            IconOption("FitnessCenter", Icons.Filled.FitnessCenter),
            IconOption("SportsMartialArts", Icons.Filled.SportsMartialArts),
            IconOption("Pool", Icons.Filled.Pool),
            IconOption("DirectionsRun", Icons.Filled.DirectionsRun),
            IconOption("DirectionsBike", Icons.Filled.DirectionsBike),
            IconOption("Hiking", Icons.Filled.Hiking),
            IconOption("Skateboarding", Icons.Filled.Skateboarding),
            IconOption("Snowboarding", Icons.Filled.Snowboarding),
            IconOption("DownhillSkiing", Icons.Filled.DownhillSkiing),
            IconOption("Surfing", Icons.Filled.Surfing),
            IconOption("Kayaking", Icons.Filled.Kayaking),
        )
    ),

    IconSection(
        title = "Medicine & health",
        options = listOf(
            IconOption("MedicalServices", Icons.Filled.MedicalServices),
            IconOption("LocalHospital", Icons.Filled.LocalHospital),
            IconOption("Vaccines", Icons.Filled.Vaccines),
            IconOption("HealthAndSafety", Icons.Filled.HealthAndSafety),
            IconOption("MonitorHeart", Icons.Filled.MonitorHeart),
            IconOption("Favorite", Icons.Filled.Favorite),
            IconOption("Bloodtype", Icons.Filled.Bloodtype),
            IconOption("Medication", Icons.Filled.Medication),
//            IconOption("MedicationLiquid", Icons.Filled.MedicationLiquid),
            IconOption("SmokingRooms", Icons.Filled.SmokingRooms),
//            IconOption("NoSmoking", Icons.Filled.NoSmoking),
            IconOption("Masks", Icons.Filled.Masks),
            IconOption("Sick", Icons.Filled.Sick),
            IconOption("Psychology", Icons.Filled.Psychology),
        )
    ),

    IconSection(
        title = "Finance",
        options = listOf(
            IconOption("CreditCard", Icons.Filled.CreditCard),
            IconOption("AttachMoney", Icons.Filled.AttachMoney),
            IconOption("AccountBalanceWallet", Icons.Filled.AccountBalanceWallet),
            IconOption("Savings", Icons.Filled.Savings),
            IconOption("BusinessCenter", Icons.Filled.BusinessCenter),
            IconOption("AccountTree", Icons.Filled.AccountTree),
            IconOption("ShowChart", Icons.Filled.ShowChart),
            IconOption("BarChart", Icons.Filled.BarChart),
            IconOption("PieChart", Icons.Filled.PieChart),
            IconOption("StackedLineChart", Icons.Filled.StackedLineChart),
            IconOption("Payments", Icons.Filled.Payments),
            IconOption("Receipt", Icons.Filled.Receipt),
        )
    ),

    IconSection(
        title = "Science & education",
        options = listOf(
            IconOption("School", Icons.Filled.School),
            IconOption("MenuBook", Icons.Filled.MenuBook),
            IconOption("Book", Icons.Filled.Book),
            IconOption("Newspaper", Icons.Filled.Newspaper),
            IconOption("Inbox", Icons.Filled.Inbox),
            IconOption("LocalOffer", Icons.Filled.LocalOffer),
            IconOption("Map", Icons.Filled.Map),
            IconOption("Science", Icons.Filled.Science),
            IconOption("Biotech", Icons.Filled.Biotech),
            IconOption("Coronavirus", Icons.Filled.Coronavirus),
            IconOption("Functions", Icons.Filled.Functions),
            IconOption("Calculate", Icons.Filled.Calculate),
            IconOption("Palette", Icons.Filled.Palette),
            IconOption("Mic", Icons.Filled.Mic),
//            IconOption("Telescope", Icons.Filled.Telescope),
            IconOption("TravelExplore", Icons.Filled.TravelExplore),
        )
    ),

    IconSection(
        title = "Transport",
        options = listOf(
            IconOption("TwoWheeler", Icons.Filled.TwoWheeler),
            IconOption("Motorcycle", Icons.Filled.Motorcycle),
            IconOption("DirectionsCar", Icons.Filled.DirectionsCar),
            IconOption("LocalShipping", Icons.Filled.LocalShipping),
            IconOption("RVHookup", Icons.Filled.RvHookup),
            IconOption("Agriculture", Icons.Filled.Agriculture),
            IconOption("AirportShuttle", Icons.Filled.AirportShuttle),
            IconOption("Train", Icons.Filled.Train),
            IconOption("DirectionsBoat", Icons.Filled.DirectionsBoat),
//            IconOption("Helicopter", Icons.Filled.Helicopter),
            IconOption("Flight", Icons.Filled.Flight),
            IconOption("Air", Icons.Filled.Air),
//            IconOption("HotAirBalloon", Icons.Filled.HotAirBalloon),
            IconOption("RocketLaunch", Icons.Filled.RocketLaunch),
            IconOption("Satellite", Icons.Filled.Satellite),
            IconOption("Speed", Icons.Filled.Speed),
            IconOption("Traffic", Icons.Filled.Traffic),
            IconOption("LocalGasStation", Icons.Filled.LocalGasStation),
            IconOption("Signpost", Icons.Filled.Signpost),
            IconOption("Anchor", Icons.Filled.Anchor),
            IconOption("Luggage", Icons.Filled.Luggage),
        )
    ),

    IconSection(
        title = "Devices",
        options = listOf(
            IconOption("Tv", Icons.Filled.Tv),
            IconOption("Laptop", Icons.Filled.Laptop),
            IconOption("DesktopWindows", Icons.Filled.DesktopWindows),
            IconOption("PhoneAndroid", Icons.Filled.PhoneAndroid),
            IconOption("Call", Icons.Filled.Call),
            IconOption("Watch", Icons.Filled.Watch),
            IconOption("Tablet", Icons.Filled.Tablet),
            IconOption("NoPhotography", Icons.Filled.NoPhotography),
            IconOption("Print", Icons.Filled.Print),
            IconOption("PresentToAll", Icons.Filled.PresentToAll),
            IconOption("Calculate", Icons.Filled.Calculate),
            IconOption("Keyboard", Icons.Filled.Keyboard),
            IconOption("SportsEsports", Icons.Filled.SportsEsports),
            IconOption("Router", Icons.Filled.Router),
            IconOption("DevicesOther", Icons.Filled.DevicesOther),
            IconOption("SdCard", Icons.Filled.SdCard),
            IconOption("Memory", Icons.Filled.Memory),
            IconOption("Toys", Icons.Filled.Toys),
            IconOption("CameraAlt", Icons.Filled.CameraAlt),
            IconOption("Speaker", Icons.Filled.Speaker),
            IconOption("Headphones", Icons.Filled.Headphones),
            IconOption("Mic", Icons.Filled.Mic),
        )
    ),

    IconSection(
        title = "Software",
        options = listOf(
            IconOption("Movie", Icons.Filled.Movie),
            IconOption("Android", Icons.Filled.Android),
            IconOption("Terminal", Icons.Filled.Terminal),
//            IconOption("Apple", Icons.Filled.Apple),
            IconOption("Window", Icons.Filled.Window),
//            IconOption("Skype", Icons.Filled.Skype),
            IconOption("PhoneInTalk", Icons.Filled.PhoneInTalk),
            IconOption("Facebook", Icons.Filled.Facebook),
//            IconOption("X", Icons.Filled.X),
//            IconOption("Instagram", Icons.Filled.Instagram),
            IconOption("ChromeReaderMode", Icons.Filled.ChromeReaderMode),
            IconOption("Explore", Icons.Filled.Explore),
            IconOption("Cloud", Icons.Filled.Cloud),
            IconOption("Email", Icons.Filled.Email),
            IconOption("Security", Icons.Filled.Security),
            IconOption("WizardHat", Icons.Filled.AutoFixHigh),
            IconOption("Casino", Icons.Filled.Casino),
            IconOption("Extension", Icons.Filled.Extension),
            IconOption("SportsEsports", Icons.Filled.SportsEsports),
        )
    ),

    IconSection(
        title = "Animals",
        options = listOf(
            IconOption("PestControlRodent", Icons.Filled.PestControlRodent),
            IconOption("CrueltyFree", Icons.Filled.CrueltyFree),
            IconOption("Pets", Icons.Filled.Pets),
            IconOption("BugReport", Icons.Filled.BugReport),
            IconOption("EmojiNature", Icons.Filled.EmojiNature),
            IconOption("EmojiAnimals", Icons.Filled.EmojiNature), // اگر EmojiAnimals نداری
        )
    ),

    IconSection(
        title = "Nature",
        options = listOf(
            IconOption("Park", Icons.Filled.Park),
            IconOption("Forest", Icons.Filled.Forest),
            IconOption("BeachAccess", Icons.Filled.BeachAccess),
            IconOption("Landscape", Icons.Filled.Landscape),
            IconOption("Yard", Icons.Filled.Yard),
            IconOption("Spa", Icons.Filled.Spa),
            IconOption("LocalFlorist", Icons.Filled.LocalFlorist),
            IconOption("Eco", Icons.Filled.Eco),
            IconOption("Waves", Icons.Filled.Waves),
            IconOption("AcUnit", Icons.Filled.AcUnit),
            IconOption("Cloud", Icons.Filled.Cloud),
            IconOption("Bolt", Icons.Filled.Bolt),
            IconOption("NightsStay", Icons.Filled.NightsStay),
            IconOption("WbSunny", Icons.Filled.WbSunny),
            IconOption("Public", Icons.Filled.Public),
            IconOption("Star", Icons.Filled.Star),
        )
    ),

    IconSection(
        title = "Tools",
        options = listOf(
            IconOption("CleaningServices", Icons.Filled.CleaningServices),
            IconOption("ContentCut", Icons.Filled.ContentCut),
            IconOption("Brush", Icons.Filled.Brush),
            IconOption("Construction", Icons.Filled.Construction),
            IconOption("Hardware", Icons.Filled.Hardware),
            IconOption("Build", Icons.Filled.Build),
            IconOption("Handyman", Icons.Filled.Handyman),
            IconOption("PrecisionManufacturing", Icons.Filled.PrecisionManufacturing),
            IconOption("Settings", Icons.Filled.Settings),
            IconOption("BuildCircle", Icons.Filled.BuildCircle),
            IconOption("Plumbing", Icons.Filled.Plumbing),
            IconOption("Carpenter", Icons.Filled.Carpenter),
            IconOption("ElectricBolt", Icons.Filled.ElectricBolt),
            IconOption("Search", Icons.Filled.Search),
            IconOption("Timeline", Icons.Filled.Timeline),
            IconOption("Alarm", Icons.Filled.Alarm),
            IconOption("HourglassEmpty", Icons.Filled.HourglassEmpty),
            IconOption("Stopwatch", Icons.Filled.Timer),
        )
    ),

    IconSection(
        title = "Signs",
        options = listOf(
            IconOption("Bedtime", Icons.Filled.Bedtime),
            IconOption("Wc", Icons.Filled.Wc),
            IconOption("Share", Icons.Filled.Share),
            IconOption("Code", Icons.Filled.Code),
            IconOption("MusicNote", Icons.Filled.MusicNote),
            IconOption("Translate", Icons.Filled.Translate),
            IconOption("Functions", Icons.Filled.Functions),
            IconOption("QrCode", Icons.Filled.QrCode),
            IconOption("YinYang", Icons.Filled.Yard), // اگر YinYang نداری، جایگزین
            IconOption("RemoveCircle", Icons.Filled.RemoveCircle),
            IconOption("Block", Icons.Filled.Block),
            IconOption("DoNotDisturbOn", Icons.Filled.DoNotDisturbOn),
            IconOption("Place", Icons.Filled.Place),
            IconOption("GpsFixed", Icons.Filled.GpsFixed),
            IconOption("Autorenew", Icons.Filled.Autorenew),
            IconOption("Warning", Icons.Filled.Warning),
            IconOption("Radioactive", Icons.Filled.Dangerous),
            IconOption("Flag", Icons.Filled.Flag),
        )
    ),

    IconSection(
        title = "Smiles",
        options = listOf(
            IconOption("SentimentVerySatisfied", Icons.Filled.SentimentVerySatisfied),
            IconOption("SentimentSatisfied", Icons.Filled.SentimentSatisfied),
            IconOption("SentimentNeutral", Icons.Filled.SentimentNeutral),
            IconOption("SentimentDissatisfied", Icons.Filled.SentimentDissatisfied),
        )
    ),
)

private val iconMap: Map<String, ImageVector> = buildIconSections()
    .flatMap { it.options }
    .associate { it.key to it.icon }

fun iconFromKey(key: String): ImageVector {
    return iconMap[key] ?: Icons.Filled.Category
}









//ایکون مود های ریمایندر
@Composable
fun reminderModeIcon(mode: ReminderMode): ImageVector {
    return when (mode) {
        ReminderMode.ALLOCATED -> Icons.Filled.Timeline
        ReminderMode.FIXED_TIME -> Icons.Filled.Anchor
        ReminderMode.POMODORO_REMINDER -> Icons.Filled.PushPin
    }
}



//ایکون مود های اسکچول
@Composable
fun scheduleModeIcon(mode: ScheduleMode): ImageVector {
    return when (mode) {
        ScheduleMode.TIME_RANGE -> Icons.Filled.DateRange
        ScheduleMode.AMOUNT_OF_TIME -> Icons.Filled.Timer
        ScheduleMode.POMODORO -> Icons.Filled.PushPin
    }
}

















val topic_iconMap= mapOf(
    "ForAppOnly" to mapOf(
        "Icons.Filled.AccountTree" to Icons.Filled.AccountTree,    // for Route
        "Icons.Filled.QuestionMark" to Icons.Filled.QuestionMark,
    ),
    "building & furniture" to mapOf(
        "Icons.Filled.House" to Icons.Filled.House,
        "Icons.Filled.Chair" to Icons.Filled.Chair,
        "Icons.Filled.Bed" to Icons.Filled.Bed,
        "Icons.Filled.Kitchen" to Icons.Filled.Kitchen,
        "Icons.Filled.Window" to Icons.Filled.Window,
        "Icons.Filled.Garage" to Icons.Filled.Garage,
        "Icons.Filled.Villa" to Icons.Filled.Villa,
        "Icons.Filled.OutdoorGrill" to Icons.Filled.OutdoorGrill,
        "Icons.Filled.Fence" to Icons.Filled.Fence,
        "Icons.Filled.Desk" to Icons.Filled.Desk,
        "Icons.Filled.Apartment" to Icons.Filled.Apartment,
        "Icons.Filled.Castle" to Icons.Filled.Castle,
    ),
    "people" to  mapOf(
        "Icons.Filled.AccountCircle" to Icons.Filled.AccountCircle,
        "Icons.Filled.Accessibility" to Icons.Filled.Accessibility,
        "Icons.Filled.Boy" to Icons.Filled.Boy,
    ),
    "food & cooking" to  mapOf(
        "Icons.Filled.Coffee" to Icons.Filled.Coffee,
        "Icons.Filled.Dining" to Icons.Filled.Dining,
        "Icons.Filled.Blender" to Icons.Filled.Blender,
        "Icons.Filled.Cake" to Icons.Filled.Cake,
    ),
    "shopping & clothes" to  mapOf(
        "Icons.Filled.Checkroom" to Icons.Filled.Checkroom,
    ),
    "sport" to  mapOf(
        "Icons.Filled.Sports" to Icons.Filled.Sports,
    ),
    "medicine & health" to  mapOf(
        "Icons.Filled.Shower" to Icons.Filled.Shower,
        "Icons.Filled.Bathtub" to Icons.Filled.Bathtub,
    ),
    "finance" to  mapOf(
        "Icons.Filled.AccountBalance" to Icons.Filled.AccountBalance,
        "Icons.Filled.AccountBalanceWallet" to Icons.Filled.AccountBalanceWallet,
        "Icons.Filled.Approval" to Icons.Filled.Approval,
        "Icons.Filled.Balance" to Icons.Filled.Balance,
        "Icons.Filled.CandlestickChart" to Icons.Filled.CandlestickChart,
        "Icons.Filled.Cases" to Icons.Filled.Cases,
    ),
    "science & education" to  mapOf(
        "Icons.Filled.AutoStories" to Icons.Filled.AutoStories,
        "Icons.Filled.Biotech" to Icons.Filled.Biotech,
    ),
    "transport" to  mapOf(
        "Icons.Filled.Agriculture" to Icons.Filled.Agriculture,
        "Icons.Filled.AirplanemodeActive" to Icons.Filled.AirplanemodeActive,
        "Icons.Filled.AirportShuttle" to Icons.Filled.AirportShuttle,
    ),
    "devices" to  mapOf(
        "Icons.Filled.Thermostat" to Icons.Filled.Thermostat,
        "Icons.Filled.EnergySavingsLeaf" to Icons.Filled.EnergySavingsLeaf,
        "Icons.Filled.Tv" to Icons.Filled.Tv,
        "Icons.Filled.AccessAlarm" to Icons.Filled.AccessAlarm,
        "Icons.Filled.AdUnits" to Icons.Filled.AdUnits,
        "Icons.Filled.AddAPhoto" to Icons.Filled.AddAPhoto,
        "Icons.Filled.AddAlert" to Icons.Filled.AddAlert,
        "Icons.Filled.Computer" to Icons.Filled.Computer,
        "Icons.Filled.ContentCut" to Icons.Filled.ContentCut,
    ),
    "software" to  mapOf(
        "Icons.Filled.Android" to Icons.Filled.Android,
        "Icons.Filled.Apps" to Icons.Filled.Apps,
        "Icons.Filled.Backup" to Icons.Filled.Backup,
        "Icons.Filled.Bluetooth" to Icons.Filled.Bluetooth,
    ),
    "animals" to  mapOf(

    ),
    "nature" to  mapOf(
        "Icons.Filled.Grass" to Icons.Filled.Grass,
        "Icons.Filled.Deck" to Icons.Filled.Deck,
        "Icons.Filled.ModeNight" to Icons.Filled.ModeNight,
        "Icons.Filled.TableBar" to Icons.Filled.TableBar,
        "Icons.Filled.BeachAccess" to Icons.Filled.BeachAccess,
        "Icons.Filled.Brightness5" to Icons.Filled.Brightness5,
        "Icons.Filled.Cloud" to Icons.Filled.Cloud,
    ),
    "tools" to  mapOf(
        "Icons.Filled.Hardware" to Icons.Filled.Hardware,
        "Icons.Filled.FireExtinguisher" to Icons.Filled.FireExtinguisher,
        "Icons.Filled.Brush" to Icons.Filled.Brush,
        "Icons.Filled.Build" to Icons.Filled.Build,
        "Icons.Filled.CameraAlt" to Icons.Filled.CameraAlt,
        "Icons.Filled.CleaningServices" to Icons.Filled.CleaningServices,
    ),
    "signs" to  mapOf(
        "Icons.Filled.Air" to Icons.Filled.Air,
        "Icons.Filled.Abc" to Icons.Filled.Abc,
        "Icons.Filled.AcUnit" to Icons.Filled.AcUnit,
        "Icons.Filled.AccountTree" to Icons.Filled.AccountTree,
        "Icons.Filled.AddAlert" to Icons.Filled.AddAlert,
        "Icons.Filled.AllInclusive" to Icons.Filled.AllInclusive,
        "Icons.Filled.Anchor" to Icons.Filled.Anchor,
        "Icons.Filled.AlternateEmail" to Icons.Filled.AlternateEmail,
        "Icons.Filled.Architecture" to Icons.Filled.Architecture,
        "Icons.Filled.AttachFile" to Icons.Filled.AttachFile,
        "Icons.Filled.AttachMoney" to Icons.Filled.AttachMoney,
        "Icons.Filled.Audiotrack" to Icons.Filled.Audiotrack,
        "Icons.Filled.AutoDelete" to Icons.Filled.AutoDelete,
        "Icons.Filled.AutoFixHigh" to Icons.Filled.AutoFixHigh,
        "Icons.Filled.Call" to Icons.Filled.Call,
        "Icons.Filled.Celebration" to Icons.Filled.Celebration,
    ),
    "smiles" to  mapOf(
        "Icons.Filled.Outlet" to Icons.Filled.Outlet,
    ),
)


val colorsOfCategory :List<Color> = listOf(
    Color(0xFFF44336),
    Color(0xFFF44D40),
    Color(0xFFF4574A),
    Color(0xFFF46B5E),

    Color(0xFFE91E63),
    Color(0xFFE91E63),
    Color(0xFFE91E63),
    Color(0xFFE91E63),

    Color(0xFF9C27B0),
    Color(0xFF9C27B0),
    Color(0xFF9C27B0),
    Color(0xFF9C27B0),

    Color(0xFF673AB7),
    Color(0xFF673AB7),
    Color(0xFF673AB7),
    Color(0xFF673AB7),

    Color(0xFF3F51B5),
    Color(0xFF3F51B5),
    Color(0xFF3F51B5),
    Color(0xFF3F51B5),

    Color(0xFF2196F3),
    Color(0xFF2196F3),
    Color(0xFF2196F3),
    Color(0xFF2196F3),

    Color(0xFF00BCD4),
    Color(0xFF00BCD4),
    Color(0xFF00BCD4),
    Color(0xFF00BCD4),

    Color(0xFF009688),
    Color(0xFF009688),
    Color(0xFF009688),
    Color(0xFF009688),

    Color(0xFF4CAF50),
    Color(0xFF4CAF50),
    Color(0xFF4CAF50),
    Color(0xFF4CAF50),

    Color(0xFFCDDC39),
    Color(0xFFCDDC39),
    Color(0xFFCDDC39),
    Color(0xFFCDDC39),

    Color(0xFFFF9800),
    Color(0xFFFF9800),
    Color(0xFFFF9800),
    Color(0xFFFF9800),

    Color(0xFFFF5722),
    Color(0xFFFF5722),
    Color(0xFFFF5722),
    Color(0xFFFF5722),

)

