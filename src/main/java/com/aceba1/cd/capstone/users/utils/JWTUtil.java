package com.aceba1.cd.capstone.users.utils;

import com.aceba1.cd.capstone.users.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.bson.types.ObjectId;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JWTUtil {
  private static Algorithm algorithm;

  public static void setAlgorithm(String secret) {
    algorithm = Algorithm.HMAC256(secret);
  }
  public static String genJWT(User user, String rawPassword, long expiryHours) {
    Instant now = Instant.now();

    return JWT.create()
      .withSubject("user-auth")
      .withIssuedAt(Date.from(now))
      .withClaim("name", user.name)
      .withClaim("email", user.email)
      .withClaim("password", rawPassword)
      .withClaim("userid", user._id.toHexString())
      .withExpiresAt(Date.from(now.plus(expiryHours, ChronoUnit.HOURS)))
      .sign(algorithm);
  }

  //TODO: Should return specific abstracting error messages
  public static User parseJWT(String jwt) {
    var decodedJWT = JWT.require(algorithm)
      .acceptExpiresAt(5)
      .build().verify(jwt);

    var user = new User(
      decodedJWT.getClaim("name").asString(),
      decodedJWT.getClaim("email").asString(),
      decodedJWT.getClaim("password").asString());
    user._id = new ObjectId(decodedJWT.getClaim("userid").asString());
    return user;
  }
}
