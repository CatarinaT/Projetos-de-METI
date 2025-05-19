import org.eclipse.mosaic.lib.enums.DriveDirection;
import org.eclipse.mosaic.lib.geo.CartesianPoint;
import org.eclipse.mosaic.lib.objects.v2x.EncodedPayload;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.lib.objects.vehicle.Emissions;

import javax.annotation.Nonnull;

public class LightMessage extends V2xMessage {

    private final String ligthcolor;

    public LightMessage(MessageRouting routing, String ligthcolor) {
        super(routing);
        this.ligthcolor = ligthcolor;
    }

    public String getLightColor() {
        return ligthcolor;
    }


    @Nonnull
    @Override
    public EncodedPayload getPayLoad() {
        return new EncodedPayload(Double.BYTES);
    }

    @Override
    public String toString() {
        return "LigthMessage{" +
                "Light = " + ligthcolor +
                '}';
    }
}
