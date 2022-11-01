package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Workmate {
   private String id;
   private String username;
   private String email;
   private String photoUrl;
   private WorkmateChosenRestaurant chosenRestaurant;
   private List<Map<String, String>> likedRestaurants;

   public Workmate() {
   }

   public Workmate(String id, String username, String email, String photoUrl, WorkmateChosenRestaurant chosenRestaurant, List<Map<String, String>> likedRestaurants) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.photoUrl = photoUrl;
      this.chosenRestaurant = chosenRestaurant;
      this.likedRestaurants = likedRestaurants;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhotoUrl() {
      return photoUrl;
   }

   public void setPhotoUrl(String photoUrl) {
      this.photoUrl = photoUrl;
   }

   public WorkmateChosenRestaurant getChosenRestaurant() {
      return chosenRestaurant;
   }

   public void setChosenRestaurant(WorkmateChosenRestaurant chosenRestaurant) {
      this.chosenRestaurant = chosenRestaurant;
   }

   public List<Map<String, String>> getLikedRestaurants() {
      return likedRestaurants;
   }

   public void setLikedRestaurants(List<Map<String, String>> likedRestaurants) {
      this.likedRestaurants = likedRestaurants;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Workmate workmate = (Workmate) o;
      return Objects.equals(id, workmate.id) && Objects.equals(username, workmate.username) && Objects.equals(email, workmate.email) && Objects.equals(photoUrl, workmate.photoUrl) && Objects.equals(chosenRestaurant, workmate.chosenRestaurant) && Objects.equals(likedRestaurants, workmate.likedRestaurants);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, email, photoUrl, chosenRestaurant, likedRestaurants);
   }

   @NonNull
   @Override
   public String toString() {
      return "Workmate2{" +
          "id='" + id + '\'' +
          ", username='" + username + '\'' +
          ", email='" + email + '\'' +
          ", photoUrl='" + photoUrl + '\'' +
          ", chosenRestaurant=" + chosenRestaurant +
          ", likedRestaurants=" + likedRestaurants +
          '}';
   }
}
