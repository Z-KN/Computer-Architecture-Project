#include "rocc.h"

extern __inline
    unsigned long 
    __attribute__((__gnu_inline__, __always_inline__, __artificial__))
rdcycle(void)
{
    unsigned long dst;
    // output into any register, likely a0
    // regular instruction:
    // asm volatile ("csrrs %0, 0xc00, x0" : "=r" (dst) );
    // regular instruction with symbolic csr and register names
    // asm volatile ("csrrs %0, cycle, zero" : "=r" (dst) );
    // pseudo-instruction:
    // asm volatile ("csrr %0, cycle" : "=r" (dst) );
    // pseudo-instruction:
    asm volatile ("rdcycle %0" : "=r" (dst) );
    return dst;
}

extern __inline
    unsigned long 
    __attribute__((__gnu_inline__, __always_inline__, __artificial__))
rdinstret(void)
{
    unsigned long dst;
    // output into any register, likely a0
    // regular instruction:
    // asm volatile ("csrrs %0, 0xc00, x0" : "=r" (dst) );
    // regular instruction with symbolic csr and register names
    // asm volatile ("csrrs %0, cycle, zero" : "=r" (dst) );
    // pseudo-instruction:
    // asm volatile ("csrr %0, cycle" : "=r" (dst) );
    // pseudo-instruction:
    asm volatile ("rdinstret %0" : "=r" (dst) );
    return dst;
}

static inline void load_sram(int idx, void *ptr)
{
	asm volatile ("fence");
	ROCC_INSTRUCTION_SS(1, (uintptr_t) ptr, idx, 0);
}

static inline void load_input(int idx, void *ptr)
{
    asm volatile ("fence");
	ROCC_INSTRUCTION_SS(1, (uintptr_t) ptr, idx, 1);
}

static inline uint64_t store_output(void *ptr)
{
    asm volatile ("fence");
    ROCC_INSTRUCTION_S(1, (uintptr_t) ptr, 2);
}

uint64_t data1 = 0x0101010101010101;
uint64_t data2 = 0x0202020202020202;
uint64_t output[8];

static inline void init()
{
    ROCC_INSTRUCTION_S(1, output, 2);
	for (int i = 0; i < 8*8/8;i++)
	{
		load_sram(i*8, &data1);
	}
}
static inline void send_input()
{
	for (int i = 0; i < 8*8/8;i++)
	{
        load_input(i * 8, &data2);
    }
}

int main(void)
{
	unsigned long inst_st = rdinstret( );
	unsigned long cycle_st = rdcycle( );
	init();
    send_input();
    unsigned long cycle_ed = rdcycle();
    unsigned long inst_ed = rdinstret( );
	printf( "inst num = %ld\n", inst_ed-inst_st);
    printf( "cycle = %ld\n", cycle_ed-cycle_st);
    // printf( "CPI = %f\n",(cycle_ed - cycle_st)*1.0 / (inst_ed - inst_st));
    return 0;
}
