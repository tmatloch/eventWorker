package pl.tmatloch.permutationworker.scaling;

import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping(path = "/queue-scale")
public class ScalingController {

    private ScalingComponent scalingComponent;

    public ScalingController(ScalingComponent scalingComponent) {
        this.scalingComponent = scalingComponent;
    }

    @PostMapping
    Map<String, Double> newScale(@RequestBody  Map<String, Double> newScales) {
        return scalingComponent.scaleByWeight(newScales);
    }

    @GetMapping
    Map<String, Double> getScales(){
        return scalingComponent.getCurrentScalePercentage();
    }
}
