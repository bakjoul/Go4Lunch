package com.bakjoul.go4lunch.data.workmates;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Workmate {
   private String id;
   private String username;
   private String email;
   private String photoUrl;

   public Workmate() {
   }

   public Workmate(String id, String username, String email, String photoUrl) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.photoUrl = photoUrl;
   }

   public String getId() {
      return id;
   }

   public String getUsername() {
      return username;
   }

   public String getEmail() {
      return email;
   }

   public String getPhotoUrl() {
      return photoUrl;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Workmate workmate = (Workmate) o;
      return Objects.equals(id, workmate.id) && Objects.equals(username, workmate.username) && Objects.equals(email, workmate.email) && Objects.equals(photoUrl, workmate.photoUrl);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, email, photoUrl);
   }

   @NonNull
   @Override
   public String toString() {
      return "Workmate{" +
          "id='" + id + '\'' +
          ", username='" + username + '\'' +
          ", email='" + email + '\'' +
          ", photoUrl='" + photoUrl + '\'' +
          '}';
   }
}
