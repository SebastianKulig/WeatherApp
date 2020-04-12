package com.example.weather.model;

import java.util.List;





public class WeatherResult{
    //kiedy mamy roznice w nazewnictwie naszym i api to mozemy uzyc adnotacji @SerializedName ("nazwa z api") wtedy gson deserializujac (zamieniajac na z oiektu json na java
    //) wartosc z "nazwa z api" wpisze do naszej zmiennej pod spodem, ale mapowanie w drugą stronę nie zadziała (choć jest na to sposób)

    public String cod;
    public int message;
    public int cnt;

    public List<ListItem> list;

    public City city;




    public WeatherResult() {
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public List<ListItem> getList() {
        return list;
    }

    public void setList(List<ListItem> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}


