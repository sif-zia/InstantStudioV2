package com.example.myapplication.widget.FGandBGWidgets


fun convertHexToRGB(hexCode: String): String {
    var hexSubstring = ""
    if(hexCode.length!=8){
        hexSubstring="00000000"
    }
    else{
        hexSubstring=hexCode
    }
    // Extract alpha, red, green, and blue components from the hexadecimal color code
    val alphaHex = hexSubstring.substring(0, 2)
    val redHex = hexSubstring.substring(2, 4)
    val greenHex = hexSubstring.substring(4, 6)
    val blueHex = hexSubstring.substring(6)

    // Convert hexadecimal components to decimal
    val red = Integer.parseInt(redHex, 16)
    val green = Integer.parseInt(greenHex, 16)
    val blue = Integer.parseInt(blueHex, 16)
    val transparency = Integer.parseInt(alphaHex, 16)

    // Construct the RGB array string
    val rgbArray = "[$red, $green, $blue, $transparency]"
    System.out.println("Color Converted: "+red+"  "+green+"  "+blue+" "+transparency)
    return rgbArray
}