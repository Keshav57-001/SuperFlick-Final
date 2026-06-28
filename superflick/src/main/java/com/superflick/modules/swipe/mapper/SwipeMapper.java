package com.superflick.modules.swipe.mapper;

import com.superflick.modules.swipe.dto.SwipeResponse;
import com.superflick.modules.swipe.entity.SwipeAction;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SwipeMapper {

    public SwipeResponse toResponse(SwipeAction swipe, boolean matched, UUID matchId) {
        if (swipe == null) {
            return null;
        }

        // Extract the safe action name string
        String actionName = (swipe.getAction() != null) ? swipe.getAction().name() : null;

        // Instantiate using standard POJO constructor initialization
        return new SwipeResponse(actionName, matched, matchId);
    }
}