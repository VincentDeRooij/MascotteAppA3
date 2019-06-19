package com.example.mascotteappa3.MascotApp.MapView;

class GPSCoordinate {

    private double latitude;
    private double longitude;

    public GPSCoordinate(double latitude, double longitude) {
        this.latitude = GPSConverter(latitude);
        this.longitude = GPSConverter(longitude);
    }

    //Method for converting NMEA GPS coordinates to those used by MapBox
    private double GPSConverter(double number) {
        String numberString = number + "";

        String[] latStringSplit = numberString.split("\\.");
        String strNumber = latStringSplit[0];
        double defNumber = 0.0;

        if(strNumber.length() > 3) {
            int firstNumber = Integer.parseInt(strNumber.substring(0, 2));
            int secondNumber = Integer.parseInt(strNumber.substring(2));

            double numberAfterComma = secondNumberWithInt(latStringSplit[1]);

            defNumber = doCalculations(firstNumber, secondNumber, numberAfterComma);
        }
        else {
            int firstNumber = Integer.parseInt(strNumber.substring(0, 1));
            int secondNumber = Integer.parseInt(strNumber.substring(1));

            double numberAfterComma = secondNumberWithInt(latStringSplit[1]);

            defNumber = doCalculations(firstNumber, secondNumber, numberAfterComma);
        }
        return defNumber;
    }

    private double doCalculations(int firstNumber, int secondNumber, double numberAfterComma) {

        double tempNumber = secondNumber * 60 + numberAfterComma;

        tempNumber = tempNumber / 3600;
        String tempNumberAsString = tempNumber + "";
        String lat = firstNumber + "." + tempNumberAsString.substring(2);
        double defNumber = Double.parseDouble(lat);

        return defNumber;
    }

    private double secondNumberWithInt(String strNumber) {
        double defNumber = 0.0;

        if(strNumber.length() > 3) {
            int firstNumber = Integer.parseInt(strNumber.substring(0, 2));
            int secondNumber = Integer.parseInt(strNumber.substring(2));

            String lat = firstNumber + "." + secondNumber;
            defNumber = Double.parseDouble(defNumber + Double.parseDouble(lat) + "");
        }
        else {
            int firstNumber = Integer.parseInt(strNumber.substring(0, 1));
            int secondNumber = Integer.parseInt(strNumber.substring(1));

            String lat = firstNumber + "." + secondNumber + "";
            defNumber = Double.parseDouble(defNumber + Double.parseDouble(lat) + "");
        }

        return defNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
