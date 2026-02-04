package at.blaulix.unstableBan;

public class TimeFormatter {
    
    public static int formatToSeconds(String timeString){
        int totalSeconds = 0;
        StringBuilder number = new StringBuilder();

        for (char c : timeString.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                if (!number.isEmpty()) {
                    int value = Integer.parseInt(number.toString());
                    switch (c) {
                        case 'd' -> totalSeconds += value * 86400;
                        case 'h' -> totalSeconds += value * 3600;
                        case 'm' -> totalSeconds += value * 60;
                        case 's' -> totalSeconds += value;
                    }
                    number.setLength(0);
                }
            }
        }
        return totalSeconds;
    }

}
