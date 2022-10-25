package com.bakjoul.go4lunch.data.workmates;

public class Workmate {
   private final String id;
   private final String username;
   private final String email;
   private final String photoUrl;

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
}
