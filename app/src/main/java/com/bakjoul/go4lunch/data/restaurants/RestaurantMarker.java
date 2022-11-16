package com.bakjoul.go4lunch.data.restaurants;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class RestaurantMarker {
    private final String id;
    private final LatLng position;
    private final String title;
    @IdRes
    private final int icon;

    public RestaurantMarker(String id, LatLng position, String title, int icon) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantMarker that = (RestaurantMarker) o;
        return icon == that.icon && Objects.equals(id, that.id) && Objects.equals(position, that.position) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, title, icon);
    }

    @NonNull
    @Override
    public String toString() {
        return "RestaurantMarker{" +
            "id='" + id + '\'' +
            ", position=" + position +
            ", title='" + title + '\'' +
            ", icon=" + icon +
            '}';
    }
}
