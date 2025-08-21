package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.IoTDataResponse;
import org.godigit.trackwise.model.IoTData;

public class IoTDataMapper {

    public static IoTDataResponse toResponseDTO(IoTData data) {
        if (data == null) {
            return null;
        }

        IoTDataResponse dto = new IoTDataResponse();
        dto.setLogId(data.getId());
        dto.setTimestamp(data.getTimestamp());
        dto.setTemperature(data.getTemperature());
        dto.setBatteryLevel(data.getBatteryLevel());
        dto.setInUse(data.getInUse());

        if (data.getAsset() != null) {
            dto.setAssetId(data.getAsset().getId());
            dto.setAssetName(data.getAsset().getName());
        }

        return dto;
    }
}