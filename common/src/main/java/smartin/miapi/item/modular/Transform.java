package smartin.miapi.item.modular;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import smartin.miapi.Miapi;

import java.io.IOException;

/**
 * A Transform represents a transformation in 3D space, including rotation, translation, and scaling.
 * It extends the Transformation class with additional utility methods.
 */
@JsonAdapter(Transform.TransformJsonAdapter.class)
public class Transform {
    public String origin;
    public final Vector3f rotation;
    public final Vector3f translation;
    public final Vector3f scale;
    /**
     * The identity bakedTransform, representing no transformation at all.
     */
    public static final Transform IDENTITY = new Transform(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));

    /**
     * Creates a new Transform with the given rotation, translation, and scale.
     *
     * @param rotation    the rotation vector, as a Vec3f
     * @param translation the translation vector, as a Vec3f
     * @param scale       the scale vector, as a Vec3f
     */
    public Transform(Vector3f rotation, Vector3f translation, Vector3f scale) {
        this.rotation = new Vector3f(rotation);
        this.translation = new Vector3f(translation);
        this.scale = new Vector3f(scale);
    }

    @Environment(EnvType.CLIENT)
    public Transform(Transformation transformation) {
        this.rotation = new Vector3f(transformation.rotation);
        this.translation = new Vector3f(transformation.translation);
        this.scale = new Vector3f(transformation.scale);
    }

    /**
     * Merges two Transformations into a new Transform. This Transform is applied first, followed by the child.
     *
     * @param child the child transformation, as a Transformation
     * @return the merged transformation, as a new Transform
     */
    public Transform merge(Transform child) {
        return Transform.merge(this, child);
    }

    @Environment(EnvType.CLIENT)
    public Transformation toTransformation() {
        return new Transformation(rotation, translation, scale);
    }

    /**
     * Merges two Transformations into a new Transform. The parent transformation is applied first, followed by the child.
     *
     * @param parent        the parent transformation, as a Transformation
     * @param originalChild the child transformation, as a Transformation
     * @return the merged transformation, as a new Transform
     */
    public static Transform merge(Transform parent, Transform originalChild) {
        Transform child = originalChild.copy();
        parent = parent.copy();
        Matrix4f parentMatrix = parent.toMatrix();

        Matrix4f childMatrix = child.toMatrix();
        childMatrix.mul(parentMatrix);
        return fromMatrix(childMatrix);
    }

    /**
     * Applies the transformation to a vector in 3D space.
     *
     * @param vector the vector to bakedTransform, as a Vec3f
     * @return the transformed vector, as a new Vec3f
     */
    public Vector3f transformVector(Vector3f vector) {
        // Apply scaling
        float x = vector.x() * scale.x();
        float y = vector.y() * scale.y();
        float z = vector.z() * scale.z();

        // Apply rotation
        float cosX = MathHelper.cos(rotation.x());
        float sinX = MathHelper.sin(rotation.x());
        float cosY = MathHelper.cos(rotation.y());
        float sinY = MathHelper.sin(rotation.y());
        float cosZ = MathHelper.cos(rotation.z());
        float sinZ = MathHelper.sin(rotation.z());

        float x2 = cosY * (sinZ * y + cosZ * x) - sinY * z;
        float y2 = sinX * (cosY * z + sinY * (sinZ * y + cosZ * x)) + cosX * (cosZ * y - sinZ * x);
        float z2 = cosX * (cosY * z + sinY * (sinZ * y + cosZ * x)) - sinX * (cosZ * y - sinZ * x);

        x = x2;
        y = y2;
        z = z2;

        // Apply translation
        x += translation.x();
        y += translation.y();
        z += translation.z();

        return new Vector3f(x, y, z);
    }

    public Matrix4f toMatrix() {
        Matrix4f matrix = new Matrix4f();

        // Set the translation (position)
        matrix.translate(translation);

        // Create quaternion rotations from Euler angles
        Quaternionf rotation = new Quaternionf().rotationXYZ(
                (float) Math.toRadians(this.rotation.x),
                (float) Math.toRadians(this.rotation.y),
                (float) Math.toRadians(this.rotation.z)
        );

        // Convert quaternion rotations to matrix rotations
        matrix.rotation(rotation);

        // Set the scale
        matrix.scale(scale);

        return matrix;
    }


    /**
     * Creates a new copy of this Transform.
     *
     * @return the new Transform copy
     */
    public Transform copy() {
        Transform copy = new Transform(
                this.rotation != null ? new Vector3f(this.rotation) : new Vector3f(0, 0, 0),
                this.translation != null ? new Vector3f(this.translation) : new Vector3f(0, 0, 0),
                this.scale != null ? new Vector3f(this.scale) : new Vector3f(1, 1, 1)
        );

        copy.origin = this.origin;
        return copy;
    }

    /**
     * Repairs a Transformation by replacing null rotation, translation, or scale vectors with default values.
     *
     * @param transformation the Transformation to repair, as a Transformation
     * @return the repaired transformation, as a new Transform
     */
    public static Transform repair(Transform transformation) {
        Vector3f parentRotation = transformation.rotation;
        if (parentRotation == null) {
            parentRotation = new Vector3f(0, 0, 0);
        }
        Vector3f parentTranslation = transformation.translation;
        if (parentTranslation == null) {
            parentTranslation = new Vector3f(0, 0, 0);
        }
        Vector3f parentScale = transformation.scale;
        if (parentScale == null) {
            parentScale = new Vector3f(1, 1, 1);
        }
        return new Transform(new Vector3f(parentRotation), new Vector3f(parentTranslation), new Vector3f(parentScale)).withOrigin(transformation.origin);
    }

    public Transform withOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    /**
     * Converts a Transformation into a model Transformation by scaling the translation vector by 1/16.
     *
     * @param transformation the Transformation to convert, as a Transformation
     * @return the new model Transformation, as a Transform
     */
    public static Transform toModelTransformation(Transform transformation) {
        Transform transform = repair(transformation);
        //TODO:enable this and change all jsons
        transform.translation.mul(1.0f / 16.0f);
        //transform.translation.multiplyComponentwise(transform.scale.getX(), transform.scale.getY(), transform.scale.getZ());
        return transform;
    }

    /**
     * Creates an AffineTransformation from this Transform.
     *
     * @return an AffineTransformation with the rotation, translation, and scale from this Transform.
     */
    @Environment(EnvType.CLIENT)
    public AffineTransformation toAffineTransformation() {
        Transform transform = this.copy();
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationXYZ(
                (float) Math.toRadians(this.rotation.x),
                (float) Math.toRadians(this.rotation.y),
                (float) Math.toRadians(this.rotation.z)
        );
        Vector3f translationVector = new Vector3f(transform.translation);
        Vector3f scaleVector = new Vector3f(transform.scale);
        if (this.rotation.x + this.rotation.y + this.rotation.z != 0) {
            Miapi.LOGGER.warn(this.rotation.toString());
            Miapi.LOGGER.error("radians " + Math.toRadians(this.rotation.x) + " , " + Math.toRadians(this.rotation.y) + " + " + (float) Math.toRadians(this.rotation.z));
            Miapi.LOGGER.error(quaternionf.toString());
        }
        return new AffineTransformation(translationVector, quaternionf, scaleVector, quaternionf);
    }

    public int[] rotateVertexData(int[] vertexData) {
        for (int i = 0; i < vertexData.length; i += 8) {
            // Extract position components from vertex data
            float x = Float.intBitsToFloat(vertexData[i]);
            float y = Float.intBitsToFloat(vertexData[i + 1]);
            float z = Float.intBitsToFloat(vertexData[i + 2]);

            // Create Vector4f representing the position (X, Y, Z, 1.0)
            Vector4f position = new Vector4f(x, y, z, 1.0f);

            // Apply the transformation to the position
            Vector4f transformedPosition = this.toMatrix().transform(position);

            // Extract the transformed position components
            float transformedX = transformedPosition.x;
            float transformedY = transformedPosition.y;
            float transformedZ = transformedPosition.z;

            // Update the vertex array with the new transformed position values
            vertexData[i] = Float.floatToIntBits(transformedX);
            vertexData[i + 1] = Float.floatToIntBits(transformedY);
            vertexData[i + 2] = Float.floatToIntBits(transformedZ);
        }
        return vertexData;
    }

    /**
     * Creates an ModelBakeSettings from this Transform
     *
     * @return a ModelBakeSettings
     */
    @Environment(EnvType.CLIENT)
    public ModelBakeSettings toModelBakeSettings() {
        Transform transform = toModelTransformation(this);
        AffineTransformation affineTransformation = transform.toAffineTransformation();
        return new ModelBakeSettings() {
            @Override
            public AffineTransformation getRotation() {
                return affineTransformation;
            }

            @Override
            public boolean isUvLocked() {
                return false;
            }
        };
    }

    public static Transform fromMatrix(Matrix4f matrix) {
        // Extract translation
        Vector3f translation = new Vector3f(matrix.m30(), matrix.m31(), matrix.m32());

        // Extract rotation (in Euler angles)
        Vector3f rotation = new Vector3f();
        rotation.x = (float) Math.toDegrees((float) Math.atan2(matrix.m21(), matrix.m22()));
        rotation.y = (float) Math.toDegrees((float) Math.atan2(-matrix.m20(), Math.sqrt(matrix.m21() * matrix.m21() + matrix.m22() * matrix.m22())));
        rotation.z = (float) Math.toDegrees((float) Math.atan2(matrix.m10(), matrix.m00()));

        // Extract scale
        Vector3f scale = new Vector3f(matrix.m00(), matrix.m11(), matrix.m22());

        if (rotation.x + rotation.y + rotation.z != 0) {
            Miapi.LOGGER.error("fromMatrix");
            Miapi.LOGGER.error(rotation.toString());
        }

        return new Transform(rotation, translation, scale);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o.getClass() != this.getClass()) {
            return false;
        } else {
            Transformation transformation = (Transformation) o;
            return this.rotation.equals(transformation.rotation) && this.scale.equals(transformation.scale) && this.translation.equals(transformation.translation);
        }
    }

    public int hashCode() {
        int i = this.rotation.hashCode();
        i = 31 * i + this.translation.hashCode();
        i = 31 * i + this.scale.hashCode();
        return i;
    }

    public static class TransformJsonAdapter extends TypeAdapter<Transform> {
        @Override
        public void write(JsonWriter jsonWriter, Transform transform) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("origin").value(transform.origin);
            writeVector3f(jsonWriter, "rotation", transform.rotation);
            writeVector3f(jsonWriter, "translation", transform.translation);
            writeVector3f(jsonWriter, "scale", transform.scale);
            jsonWriter.endObject();
        }

        @Override
        public Transform read(JsonReader jsonReader) throws IOException {
            Vector3f rotation = null;
            Vector3f translation = null;
            Vector3f scale = null;
            String origin = null;

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("origin".equals(name)) {
                    origin = jsonReader.nextString();
                } else if ("rotation".equals(name)) {
                    rotation = readVector3f(jsonReader);
                } else if ("translation".equals(name)) {
                    translation = readVector3f(jsonReader);
                } else if ("scale".equals(name)) {
                    scale = readVector3f(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            // Ensure non-null values for final fields
            if (rotation == null) {
                rotation = new Vector3f();
            }
            if (translation == null) {
                translation = new Vector3f();
            }
            if (scale == null) {
                scale = new Vector3f();
            }
            Transform transform = new Transform(rotation, translation, scale);
            transform.origin = origin;
            return transform;
        }

        private void writeVector3f(JsonWriter jsonWriter, String name, Vector3f vector3f) throws IOException {
            jsonWriter.name(name);
            jsonWriter.beginArray();
            jsonWriter.value(vector3f.x);
            jsonWriter.value(vector3f.y);
            jsonWriter.value(vector3f.z);
            jsonWriter.endArray();
        }

        private Vector3f readVector3f(JsonReader jsonReader) throws IOException {
            Vector3f vector3f = new Vector3f();

            if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                // Read as an array
                jsonReader.beginArray();
                vector3f.x = (float) jsonReader.nextDouble();
                vector3f.y = (float) jsonReader.nextDouble();
                vector3f.z = (float) jsonReader.nextDouble();
                jsonReader.endArray();
            } else if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                // Read as an object with components
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String propName = jsonReader.nextName();
                    if ("x".equals(propName)) {
                        vector3f.x = (float) jsonReader.nextDouble();
                    } else if ("y".equals(propName)) {
                        vector3f.y = (float) jsonReader.nextDouble();
                    } else if ("z".equals(propName)) {
                        vector3f.z = (float) jsonReader.nextDouble();
                    }
                }
                jsonReader.endObject();
            }

            return vector3f;
        }
    }
}
