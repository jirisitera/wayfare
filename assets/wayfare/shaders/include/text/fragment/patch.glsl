#elif defined(IS_GUI)
if (renderCursor > 0.5) {
    vec4 cursorTexture = texture(Sampler0, cursorUV);
    if (cursorTexture.a < 0.1) {
        discard;
    }
    fragColor = cursorTexture;
} else {
    fragColor = color * ColorModulator;
}
