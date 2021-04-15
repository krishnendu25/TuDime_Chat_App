package com.TuDime.util;

/**
 * Created by Batuhan Özkaya on 15.08.2017.
 */

public class AppUtility {
    /** FOR DATE-TIME DIFFERENCE **/
    public enum TimeDifference{
        SECOND(0),
        MINUTE(1),
        HOUR(2),
        DAY(3),
        MONTH(4),
        YEAR(5);

        private final int timeDifference;

        TimeDifference(int timeDifference) {
            this.timeDifference = timeDifference;
        }
    }

    /** DATE-TIME DIFFERENCE - START **/
    /** WITH UNIX-TIMESTAMP - START **/
    public static long DateTimeDifference(long currentTime, long previousTime, TimeDifference timeDifference) {
        if(timeDifference.timeDifference == 0){ //DIFFERENCE WITH SECONDS
            return (currentTime - previousTime) / 1000;
        }else if(timeDifference.timeDifference == 1){ //DIFFERENCE WITH MINUTES
            return ((currentTime - previousTime) / 1000) / 60;
        }else if(timeDifference.timeDifference == 2){ //DIFFERENCE WITH HOURS
            return (((currentTime - previousTime) / 1000) / 60) / 60;
        }else if(timeDifference.timeDifference == 3){ //DIFFERENCE WITH DAYS
            return ((((currentTime - previousTime) / 1000) / 60) / 60) / 24;
        }else if(timeDifference.timeDifference == 4){ //DIFFERENCE WITH MONTHS
            return (((((currentTime - previousTime) / 1000) / 60) / 60) / 24) / 30;
        }else{ //GETTING DIFFERENCE WITH YEARS
            return ((((((currentTime - previousTime) / 1000) / 60) / 60) / 24) / 30) / 365;
        }
    }
    /** WITH UNIX-TIMESTAMP - FINISH **/
    /** DATE-TIME DIFFERENCE - FINISH **/
}