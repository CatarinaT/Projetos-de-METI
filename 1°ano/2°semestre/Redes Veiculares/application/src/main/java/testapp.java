import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.AdHocModuleConfiguration;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.os.RoadSideUnitOperatingSystem;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.lib.enums.AdHocChannel;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.TIME;

import java.util.*;

public class testapp extends AbstractApplication<RoadSideUnitOperatingSystem> implements CommunicationApplication{
    private boolean FirstMessage = true;
    private Map<String,String> TodosCarros = new HashMap<>();

    private List<String> recentDirections = new ArrayList<>();
    public void sendMessageToAllCars(String message) {
        for (String carName : TodosCarros.keySet()) {
            sendMessageToCar(carName, message);
        }
    }

    public void sendMessageToCar(String carName, String Message){
        MessageRouting routing = getOs().getCellModule().createMessageRouting().topoCast(carName);
        getOs().getCellModule().sendV2xMessage(new LightMessage(routing,Message));
    }


    @Override
    public void onStartup() {
        getLog().infoSimTime(this, "Initialize RSU2");
        getOs().getAdHocModule().enable(new AdHocModuleConfiguration()
                .addRadio()
                .channel(AdHocChannel.SCH1)
                .power(600)
                .create());

        getOs().getCellModule().enable();
    }

    @Override
    public void onShutdown() {
        this.getLog().infoSimTime(this, "Shutdown RSU2", new Object[0]);
    }

    @Override
    public void processEvent(Event event) throws Exception {
        sendMessageToAllCars("VERMELHO");

        int countEsquerda= 0, countDireita= 0, countCima= 0, countBaixo = 0;

        // Itera sobre o HashMap e conta o número de carros em cada direção
        for (String direction : TodosCarros.values()) {
            switch (direction) {
                case "Esquerda":
                    countEsquerda++;
                    break;
                case "Direita":
                    countDireita++;
                    break;
                case "Cima":
                    countCima++;
                    break;
                case "Baixo":
                    countBaixo++;
                    break;
            }
        }

        Map<String, Integer> directionCounts = new HashMap<>();
        directionCounts.put("Esquerda", countEsquerda);
        directionCounts.put("Direita", countDireita);
        directionCounts.put("Cima", countCima);
        directionCounts.put("Baixo", countBaixo);

        String directionWithMostCars = directionCounts.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sort by count descending
                .filter(entry -> !recentDirections.contains(entry.getKey())) // Exclude recently used directions
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("null");

        int maxCount = 0;
        switch (directionWithMostCars){
            case "Esquerda":
                maxCount = countEsquerda;
                break;

            case "Direita":
                maxCount = countDireita;
                break;

            case "Cima":
                maxCount = countCima;
                break;

            case "Baixo":
                maxCount = countBaixo;
                break;

            default:
                maxCount = 0;
                break;
        }

        recentDirections.add(directionWithMostCars);
        if (recentDirections.size() > 3) { // Maintain a list of last 3 directions
            recentDirections.remove(0);
        }

        if(maxCount != 0) {



            for (Map.Entry<String, String> entry : TodosCarros.entrySet()) {
                String carName = entry.getKey();
                String direction = entry.getValue();

                if (direction.equals(directionWithMostCars)) {
                    sendMessageToCar(carName, "VERDE");
                }
            }

            Iterator<Map.Entry<String, String>> iterator = TodosCarros.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (entry.getValue().equals(directionWithMostCars)) {
                    iterator.remove();
                }
            }

            if (maxCount > 10) {
                maxCount = 10; //apenas para limitar o tempo maximo a 40 segundos
            }

            getOs().getEventManager().addEvent(getOs().getSimulationTime() + (maxCount * 3 * TIME.SECOND), this);
        }else{
            getOs().getEventManager().addEvent(getOs().getSimulationTime() + (50 * TIME.MILLI_SECOND), this);
        }


    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        final V2xMessage msg = receivedV2xMessage.getMessage();

        if ((msg instanceof StatusMessage)){
            String Road = ((StatusMessage) msg).getRoad();
            switch (Road) {
                case "11580205_103222486_103236777":
                    TodosCarros.put(msg.getRouting().getSource().getSourceName(),"Esquerda");
                    break;

                case "11580205_103236778_103236777":
                    TodosCarros.put(msg.getRouting().getSource().getSourceName(),"Direita");
                    break;

                case "11580277_103227786_103236777":
                    TodosCarros.put(msg.getRouting().getSource().getSourceName(),"Cima");
                    break;

                case "11580277_103237259_103236777":
                    TodosCarros.put(msg.getRouting().getSource().getSourceName(),"Baixo");
                    break;

                default:
                    if(TodosCarros.containsKey(msg.getRouting().getSource().getSourceName())){
                        TodosCarros.remove(msg.getRouting().getSource().getSourceName());
                        sendMessageToCar(msg.getRouting().getSource().getSourceName(), "VERDE");
                    }

            }
            if (this.FirstMessage) {
                this.FirstMessage = false;
                getOs().getEventManager().addEvent(getOs().getSimulationTime() + (100 * TIME.MILLI_SECOND), this);
            }
        }
    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement receivedAcknowledgement) {

    }

    @Override
    public void onCamBuilding(CamBuilder camBuilder) {

    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {

    }
}
