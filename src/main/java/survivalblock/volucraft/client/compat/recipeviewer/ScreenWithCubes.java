package survivalblock.volucraft.client.compat.recipeviewer;

import org.joml.Vector2f;
import survivalblock.volucraft.client.render.CubeModel;

public interface ScreenWithCubes {
    CubeModel volucraft$getCubeModel();
    CubeModel volucraft$getCubeModelWithItem();
    long volucraft$calculateTimeOpen();
    Vector2f volucraft$getRotation();
}
