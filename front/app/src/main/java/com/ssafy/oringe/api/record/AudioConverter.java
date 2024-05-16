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
                String errorOutput = Config.getLastCommandOutput();
                listener.onConversionFailed("Conversion failed: code " + returnCode + ", error: " + errorOutput);
                System.out.println("Conversion failed: code " + returnCode + ", error: " + errorOutput);
            }
        });
    }

    public interface ConversionListener {
        void onConversionSuccess(File convertedFile);
        void onConversionFailed(String error);
    }
}