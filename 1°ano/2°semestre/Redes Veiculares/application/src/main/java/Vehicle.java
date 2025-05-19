import org.eclipse.mosaic.fed.application.ambassador.navigation.RoadPositionFactory;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.AdHocModuleConfiguration;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.VehicleApplication;
import org.eclipse.mosaic.fed.application.app.api.os.VehicleOperatingSystem;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.interactions.vehicle.VehicleSensorActivation.SensorType;
import org.eclipse.mosaic.interactions.vehicle.VehicleStop;
import org.eclipse.mosaic.lib.enums.AdHocChannel;
import org.eclipse.mosaic.lib.enums.DriveDirection;
import org.eclipse.mosaic.lib.geo.GeoCircle;
import org.eclipse.mosaic.lib.geo.GeoPoint;
import org.eclipse.mosaic.lib.objects.road.IRoadPosition;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.lib.objects.vehicle.VehicleData;
import org.eclipse.mosaic.lib.util.SerializationUtils;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.TIME;


import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Random;

import static org.eclipse.mosaic.lib.enums.VehicleStopMode.STOP;

public class Vehicle extends AbstractApplication<VehicleOperatingSystem> implements VehicleApplication, CommunicationApplication {
    Random random = new Random();
    private String type = "car";
    private String dimensions = String.format(
            "comprimento: %.2f m; Largura: %.2f m; Altura: %.2f m",
            3.6 + (4.8 - 3.6) * random.nextDouble(),
            1.6 + (1.9 - 1.6) * random.nextDouble(),
            1.4 + (1.5 - 1.4) * random.nextDouble()
            );
    private double weight = 1000 + (1600 - 1000) * random.nextDouble(); //gera um peso entre 1000 e 1600 kilos
    private String latitude;
    private String longitude;
    private double speed;
    private double acceleration;
    private DriveDirection direction;

    private boolean lastverde;

    //private boolean wetRoad;
    //private boolean foggy;
    private String road;

    private String lastRoad;

    @Override    
    public void onStartup() {
        this.lastverde = true;
        getLog().infoSimTime(this, "Initialize application");
        getOs().getAdHocModule().enable(new AdHocModuleConfiguration()
                .addRadio()
                .channel(AdHocChannel.SCH1)
                .power(300)
                .create());

        getOs().getCellModule().enable();

        //getOs().getEventManager().addEvent(getOs().getSimulationTime() + (100*TIME.MILLI_SECOND), this);
    } 
    
    @Override    
    public void onVehicleUpdated(@Nullable VehicleData previousVehicleData,@Nonnull VehicleData updatedVehicleData) {
        road = getOs().getVehicleData().getRoadPosition().getConnectionId();
        latitude = String.format("%.6f", updatedVehicleData.getPosition().getLatitude());
        longitude = String.format("%.6f", updatedVehicleData.getPosition().getLongitude());
        speed = updatedVehicleData.getSpeed();
        acceleration = updatedVehicleData.getLongitudinalAcceleration();
        direction = updatedVehicleData.getDriveDirection();



        MessageRouting routing = getOs().getAdHocModule().createMessageRouting().topoBroadCast(10);
        getOs().getAdHocModule().sendV2xMessage(new StatusMessage(routing, type, dimensions, weight, latitude, longitude, speed, acceleration, direction, road));
    } 
    
    @Override
    public void onShutdown() {
        this.getLog().infoSimTime(this, "Shutdown application", new Object[0]);
     }
     
     @Override
     public void processEvent(Event event) throws Exception {

     }

      @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        final V2xMessage msg = receivedV2xMessage.getMessage();

        if ((msg instanceof LightMessage) ){
            if(((LightMessage) msg).getLightColor() == "VERMELHO"){
                if(this.lastverde) {
                    this.lastverde = false;
                    IRoadPosition roadPosition = RoadPositionFactory.createAtEndOfRoute(Collections.singletonList(getOs().getRoadPosition().getConnection().getId()), 0);

                    getOs().stop(roadPosition, STOP, 100 * TIME.MINUTE);
                    getLog().info("Received message from {} : {}", msg.getRouting().getSource().getSourceName(), msg);

                }
            }else{
                this.lastverde = true;

                IRoadPosition roadPosition = RoadPositionFactory.createAtEndOfRoute( Collections.singletonList(getOs().getRoadPosition().getConnection().getId()), 0);
                getOs().stop(roadPosition, STOP, 0 * TIME.MILLI_SECOND); //Makes the car leave "stop" position
                getLog().info("Received message from {} : {}", msg.getRouting().getSource().getSourceName(), msg);
            }
        }
        if ((msg instanceof StatusMessage)){
            if((( ((StatusMessage) msg).getRoad().equals(this.road)) )) { //só vai processar mensagens de veículos na mesma rua/direção
                getLog().info("Received message from {} : {}", msg.getRouting().getSource().getSourceName(), msg);
            }
        }
    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement acknowledgedMessage) {
    }

    @Override
    public void onCamBuilding(CamBuilder camBuilder) {
    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {
    }

}
