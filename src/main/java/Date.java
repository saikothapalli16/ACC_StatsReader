public class Date {
    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {

        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String toString(){
        return year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day);
    }

    public String getMonth(){
        return String.format("%02d", this.month);
    }

    public String getYear(){
        return String.format("%04d", this.year);
    }

    public boolean isLeapYear(){
        return this.year % 4 == 0;
    }

    public void incrementDate(){
        if(this.month == 12 && this.day == 31){
            this.year++;
            this.day = 1;
            this.month = 1;

        }
        else if((this.month == 4 || this.month == 6 || this.month == 9 || this.month == 11) && this.day == 30){
           this.month++;
           this.day = 1;
        }
        else if((this.month == 2 && this.day == 29 && isLeapYear()) || (this.month == 2 && this.day == 28 && !isLeapYear())){
            this.month++;
            this.day = 1;
        }
        else if(this.day == 31){
            this.month++;
            this.day = 1;
        }
        else{
            this.day++;
        }
    }

    public int daysInterval(Date endDate){
        int counter = 0;
        Date currentDate = new Date(this.year, this.month, this.day);

        while (!currentDate.equals(endDate)) {
            currentDate.incrementDate();
            counter++;
        }
        return counter;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Date date = (Date) obj;
        return year == date.year && month == date.month && day == date.day;
    }



}
