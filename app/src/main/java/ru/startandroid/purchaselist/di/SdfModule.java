package ru.startandroid.purchaselist.di;

import java.text.SimpleDateFormat;
import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.di.annotations.PerApp;

@Module
public class SdfModule {
    @PerApp
    @Provides
    SimpleDateFormat getSdf(){
        return new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    }
}
