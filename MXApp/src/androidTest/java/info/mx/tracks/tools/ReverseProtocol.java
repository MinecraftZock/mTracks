package info.mx.tracks.tools;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import java.util.LinkedList;

import androidx.test.espresso.action.AdapterViewProtocol;
import androidx.test.espresso.util.EspressoOptional;

import static androidx.test.espresso.action.AdapterViewProtocols.standardProtocol;

public class ReverseProtocol implements AdapterViewProtocol {
    private final AdapterViewProtocol delegate = standardProtocol();

    @Override
    public Iterable getDataInAdapterView(AdapterView<? extends Adapter> av) {
        LinkedList result = new LinkedList<>();
        for (AdaptedData data : delegate.getDataInAdapterView(av)) {
            result.addFirst(data);
        }
        return result;
    }

    @Override
    public void makeDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData data) {

    }

    @Override
    public boolean isDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData adaptedData) {
        return false;
    }

    @Override
    public EspressoOptional getDataRenderedByView(AdapterView<? extends Adapter> av, View v) {
        return delegate.getDataRenderedByView(av, v);
    }

}
