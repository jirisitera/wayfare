#ifdef IS_GUI
// check for id pixel at 0,0
renderCursor = (texelFetch(Sampler0, ivec2(0, 0), 0).rgb == vec3(234.0, 123.0, 213.0) / 255.0) ? 1.0 : 0.0;
if (renderCursor > 0.5) {
    vec2 textureSize = vec2(textureSize(Sampler0, 0));
    vec2 currentPixel = UV0 * textureSize;

    // determine vertex corner
    bool isLeft = currentPixel.x < 128.0;
    bool isTop = currentPixel.y < 128.0;

    // calculate position offset (for 8x8 texture)
    float width = abs(ProjMat[0][0]) * 8.0;
    float height = abs(ProjMat[1][1]) * 8.0;

    // x goes from right to left, y goes from top to bottom
    float xOffset = isLeft ? -width / 2.0 : width / 2.0;
    float yOffset = isTop ? height / 2.0 : -height / 2.0;

    // get target screen x and y from input color
    float red = floor(Color.r * 255.0 + 0.5);
    float green = floor(Color.g * 255.0 + 0.5);
    float blue = floor(Color.b * 255.0 + 0.5);
    float x = mix(-1.0, 1.0, (red * 16.0 + floor(blue / 16.0)) / 4095.0);
    float y = mix(1.0, -1.0, (green * 16.0 + mod(blue, 16.0)) / 4095.0);

    // apply offsets to move to cursor position
    gl_Position.xy = vec2(x + xOffset, y + yOffset);

    // map to cursor texture (from pixel 1,1 to 9,9)
    cursorUV = vec2(isLeft ? 1.0 : 9.0, isTop ? 1.0 : 9.0) / textureSize;
    texCoord0 = cursorUV;
    vertexColor = vec4(1.0, 1.0, 1.0, Color.a);
}
#endif
