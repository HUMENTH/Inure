package app.simple.inure.interfaces.adapters;

import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import app.simple.inure.models.BatchPackageInfo;
import app.simple.inure.models.BatteryOptimizationModel;
import app.simple.inure.models.BootManagerModel;
import app.simple.inure.models.Note;
import app.simple.inure.models.StackTrace;

public interface AdapterCallbacks {
    default void onAppClicked(@NonNull PackageInfo packageInfo, @NonNull ImageView icon) {
    
    }
    
    default void onAppLongPressed(@NonNull PackageInfo packageInfo, @NonNull ImageView icon) {
    
    }
    
    default void onSearchPressed(@NonNull View view) {
    
    }
    
    default void onFilterPressed(@NonNull View view) {
    
    }
    
    default void onSortPressed(@NonNull View view) {
    
    }
    
    default void onSettingsPressed(@NonNull View view) {
    
    }
    
    default void onInfoPressed(@NonNull View view) {
    
    }
    
    default void onNoteDelete(@NonNull View view, Note note) {
    
    }
    
    default void onBatchChanged(@NonNull BatchPackageInfo batchPackageInfo) {
    
    }
    
    default void onNoteClicked(@NonNull Note note, @NotNull View view) {
    
    }
    
    default void onNoteLongClicked(@NonNull Note note, int position, View view) {
    
    }
    
    default void onStackTraceClicked(@NonNull StackTrace stackTrace) {
    
    }
    
    default void onStackTraceLongClicked(@NonNull StackTrace stackTrace, View view, int position) {
    
    }
    
    default void onBatteryOptimizationClicked(@NotNull View view, @NonNull BatteryOptimizationModel batteryOptimizationModel, int position) {
    
    }
    
    default void onBootComponentClicked(@NonNull View view, @NonNull BootManagerModel bootManagerModel, int position, @NotNull ImageView icon) {
        
    }
    
    default void onBootComponentLongClicked(@NonNull View view, @NonNull BootManagerModel bootManagerModel, int position, @NotNull ImageView icon) {
    
    }
    
    default void onGridClicked(@NonNull View view) {
    
    }
    
    default void onApkClicked(@NonNull View view, int position, @NotNull ImageView icon) {
    
    }
    
    default void onApkLongClicked(@NonNull View view, int position, @NotNull ImageView icon) {
    
    }
    
    default void onSelectionChanged() {
    
    }
}
