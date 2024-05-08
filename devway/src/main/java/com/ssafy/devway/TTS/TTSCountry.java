package com.ssafy.devway.TTS;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TTSCountry {

    // United States
    US_A_MALE("en-US-Standard-A"),
    US_B_MALE("en-US-Standard-B"),
    US_C_FEMALE("en-US-Standard-C"),
    US_D_MALE("en-US-Standard-D"),
    US_E_FEMALE("en-US-Standard-E"),
    US_F_FEMALE("en-US-Standard-F"),
    US_G_FEMALE("en-US-Standard-G"),
    US_H_FEMALE("en-US-Standard-H"),
    US_I_MALE("en-US-Standard-I"),
    US_J_MALE("en-US-Standard-J"),
    // United Kingdom
    UK_A_FEMALE("en-GB-Standard-A"),
    UK_B_MALE("en-GB-Standard-B"),
    UK_C_FEMALE("en-GB-Standard-C"),
    UK_D_MALE("en-GB-Standard-D"),
    UK_F_FEMALE("en-GB-Standard-F"),
    // Australia
    AU_A_FEMALE("en-AU-Standard-A"),
    AU_B_MALE("en-AU-Standard-B"),
    AU_C_FEMALE("en-AU-Standard-C"),
    AU_D_MALE("en-AU-Standard-D"),
    ;

    public final String textMode;
}
