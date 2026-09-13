package eu.konggdev.strikemaps.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import eu.konggdev.strikemaps.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class ComponentHolderActivity extends AppCompatActivity {
    private List<Component> components = new ArrayList<>();

    public final <T extends Component> T getComponent(Class<T> type) {
        return components.stream()
                .filter(type::isInstance)
                .findFirst()
                .map(type::cast)
                .orElse(null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
    }
}
