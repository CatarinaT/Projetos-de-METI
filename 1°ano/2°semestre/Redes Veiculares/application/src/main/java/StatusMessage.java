import org.eclipse.mosaic.lib.enums.DriveDirection;
import org.eclipse.mosaic.lib.geo.CartesianPoint;
import org.eclipse.mosaic.lib.objects.v2x.EncodedPayload;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.lib.objects.vehicle.Emissions;

import javax.annotation.Nonnull;

public class StatusMessage extends V2xMessage {

    private final String type;
    private final String dimensions;
    private final double weight;
    private final String latitude;
    private final String longitude;
    private final double speed;
    private final double acceleration;
    private final DriveDirection direction;
    //private final boolean wetRoad;
    //private final boolean foggy;
    private final String road;

    public StatusMessage(MessageRouting routing, String type, String dimensions, double weight, String latitude, String longitude,
                         double speed, double acceleration, DriveDirection direction, String road ) {
        super(routing);
        this.type = type;
        this.dimensions = dimensions;
        this.weight = weight;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.acceleration = acceleration;
        this.direction = direction;
        this.road = road;
    }

    public String getType() {
        return type;
    }

    public String getDimensions() {
        return dimensions;
    }

    public double getWeight() {
        return weight;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public DriveDirection getDirection() {
        return direction;
    }
    public String getRoad() {
        return road;
    }

    @Nonnull
    @Override
    public EncodedPayload getPayLoad() {
        return new EncodedPayload(Double.BYTES);
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "type = " + type +
                ", dimensions = " + dimensions +
                ", weight = " + weight +
                ", latitude = " + latitude +
                ", longitude = " + longitude +
                ", speed (m/s) = " + speed +
                ", accelaration (m^2/s) = " + acceleration +
                ", direction = " + direction +
                ", road = " + road +
                '}';
    }
}