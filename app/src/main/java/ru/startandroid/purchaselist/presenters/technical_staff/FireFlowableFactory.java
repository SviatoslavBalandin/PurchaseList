package ru.startandroid.purchaselist.presenters.technical_staff;

import com.google.firebase.database.DataSnapshot;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class FireFlowableFactory {

    public static Flowable<DataSnapshot> getFireFlowable(Iterable<DataSnapshot> linkData){

        return Flowable.create(emitter -> {

            for (DataSnapshot snapshot : linkData) {

                emitter.onNext(snapshot);
            }
            emitter.onComplete();

        }, BackpressureStrategy.BUFFER);
    }
}
