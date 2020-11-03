package co.edu.unal.reto8

class StringConverter {


    fun fromArray(strings: List<String>): String {
        var str = ""
        for (s in strings) str += ("$s,")
        return str
    }

    fun toArray(concatStrings: String): List<String> {
        val myStrings : ArrayList<String> = ArrayList()
        for (s in concatStrings.split(",")) myStrings.add(s)
        return myStrings
    }

}