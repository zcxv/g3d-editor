#extension GL_EXT_gpu_shader4 : require

#define BLOCK_TYPE_MULTI_LAYER 2
#define BLOCK_TYPE_COMPLEX 1
#define BLOCK_TYPE_FLAT 0

#define MAYBE_MAKE_DARKER
#define MAYBE_MAKE_DARKER_FACTOR 0.85

uniform vec4 cell_colors[13];
uniform int block_data[64];
uniform int cell_positions[64];
uniform int cell_data;
uniform vec2 block_position;
uniform int block_type;

varying vec2 texCoord;

void main(void) 
{
	vec4 pos = gl_Vertex;
	int color;
	int height;
	
	if (block_type != BLOCK_TYPE_FLAT)
	{
		int x = int(pos.x);
		int y = int(pos.z);
		int index = x * 8 + y;
		int data = block_data[index];
		int nswe = data >> 4 & 15;
		height = (data >> 4 & -16) >> 1;
		color = data & 15;
		
		if (gl_MultiTexCoord0.s != 0.0)
		{
			float u1 = float(nswe / 4) * 0.25;
			float v1 = float(nswe % 4) * 0.25;
			
			if (gl_MultiTexCoord0.s == 0.1)
			{
				texCoord.s = u1;
			}
			else
			{
				texCoord.s = u1 + 0.25;
			}
			
			if (gl_MultiTexCoord0.t == 0.3)
			{
				texCoord.t = v1;
			}
			else
			{
				texCoord.t = v1 + 0.25;
			}
		}
		else
		{
			texCoord.s = 0.0;
			texCoord.t = 0.0;
		}
		
		if (block_type == BLOCK_TYPE_MULTI_LAYER)
		{
			int position = cell_positions[index];
			pos.x -= float(x);
			pos.z -= float(y);
			pos.x = pos.x + block_position.x + float(position & 65535);
			pos.z = pos.z + block_position.y + float(position >> 16 & 65535);
		}
		else
		{
			pos.x += block_position.x;
			pos.z += block_position.y;
		}
	}
	else
	{
		height = cell_data >> 4;
		color = cell_data & 15;
		
		texCoord = vec2(gl_MultiTexCoord0);
		
		pos.x += block_position.x;
		pos.z += block_position.y;
	}
	
	pos.y += float(height) / 16.0;
	gl_Position = gl_ModelViewProjectionMatrix * pos;
	gl_FrontColor = cell_colors[color];
	
	#ifdef MAYBE_MAKE_DARKER
		if ((int(block_position.x) >> 3) % 2 != (int(block_position.y) >> 3) % 2)
			gl_FrontColor.rgb *= MAYBE_MAKE_DARKER_FACTOR;
	#endif
}