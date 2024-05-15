package com.ssafy.oringe.api.record;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

public class AudioConverter {

    public static void convertToWav(File inputFile, File outputFile, ConversionListener listener) {
        String[] command = {"-i", inputFile.getAbsolutePath(), outputFile.getAbsolutePath()};
        FFmpeg.executeAsync(command, (executionId, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                listener.onConversionSuccess(outputFile);
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                listener.onConversionFailed("Conversion cancelled");
            } else {
                listener.onConversionFailed("Conversion failed with returnCode: " + returnCode);
            }
        });
    }

    public interface ConversionListener {
        void onConversionSuccess(File convertedFile);
        void onConversionFailed(String error);
    }
}