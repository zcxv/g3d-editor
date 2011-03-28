uniform sampler2D nswe_texture;

varying vec2 texCoord;

void main(void)
{
   vec4 texColor = texture2D(nswe_texture, texCoord);
   gl_FragColor = texColor * gl_Color;
}