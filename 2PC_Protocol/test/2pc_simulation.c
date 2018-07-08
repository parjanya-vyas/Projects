#include "ot_header.h"
#include "ot_sender.h"
#include "ot_receiver.h"
#include "../include/justGarble.h"

int checkfnOrOfAnds(int *a, int *outputs, int n) {
	outputs[0] = (a[0] & a[1] & a[2] & a[3]) | (a[4] & a[5] & a[6] & a[7]);
	return outputs[0];
}

int checkfnAndOfOrs(int *a, int *outputs, int n) {
	outputs[0] = (a[0] | a[1]) & (a[2] | a[3]);
	return outputs[0];
}

void print128_num(__m128i var)
{
    uint16_t *val = (uint16_t*) &var;
    printf("Numerical: %i %i %i %i %i %i %i %i \n", 
           val[0], val[1], val[2], val[3], val[4], val[5], 
           val[6], val[7]);
}

void getRandomInputs(int inp[], int start, int end)
{
    for (int i = start; i < end; i++) {
        inp[i] = rand() % 2;
    }
}

void sendBobLabels(InputLabels inputLabels, int num_alice_inp, int n) {
    unsigned char *l1 = (unsigned char *)memalign(128, 16);
    unsigned char *l2 = (unsigned char *)memalign(128, 16);

    for(int i=num_alice_inp; i<n; i++) {
        _mm_store_si128((__m128i *)l1, inputLabels[2 * i]);
        _mm_store_si128((__m128i *)l2, inputLabels[2 * i + 1]);
        send_to_ot(l1, l2, 128, 128);
    }
}

void extractBobLabels(ExtractedLabels extractedLabels, int* inputBits, int start, int end) {
    unsigned char *l = (unsigned char *)memalign(128, 16);
    for (int i = start; i < end; i++) {
        receive_from_ot(inputBits[i-start], l);
        extractedLabels[i] = _mm_loadu_si128((__m128i *)l);
    }
}

int createAndOfOrsCircuit(GarbledCircuit *garbledCircuit,
		GarblingContext *garblingContext, int n, int* inputs, int* outputs) {
	int i;
    int newInternalWire[3];

    newInternalWire[0] = getNextWire(garblingContext);
    ORGate(garbledCircuit, garblingContext, inputs[0],
					inputs[1], newInternalWire[0]);

    newInternalWire[1] = getNextWire(garblingContext);
    ORGate(garbledCircuit, garblingContext, inputs[2],
					inputs[3], newInternalWire[1]);

    newInternalWire[2] = getNextWire(garblingContext);
    ANDGate(garbledCircuit, garblingContext, newInternalWire[0],
					newInternalWire[1], newInternalWire[2]);

    outputs[0] = newInternalWire[2];

    return 0;
}

int createOrOfAndsCircuit(GarbledCircuit *garbledCircuit,
		GarblingContext *garblingContext, int n, int* inputs, int* outputs) {
	int i;
    int newInternalWire[7];

    for (i = 0; i < n; i+=2) {
        newInternalWire[i/2] = getNextWire(garblingContext);
        ANDGate(garbledCircuit, garblingContext, inputs[i],
					inputs[i + 1], newInternalWire[i/2]);
    }

    newInternalWire[4] = getNextWire(garblingContext);
    ANDGate(garbledCircuit, garblingContext, newInternalWire[0],
					newInternalWire[1], newInternalWire[4]);

    newInternalWire[5] = getNextWire(garblingContext);
    ANDGate(garbledCircuit, garblingContext, newInternalWire[2],
					newInternalWire[3], newInternalWire[5]);

    newInternalWire[6] = getNextWire(garblingContext);
    ORGate(garbledCircuit, garblingContext, newInternalWire[4],
					newInternalWire[5], newInternalWire[6]);

    outputs[0] = newInternalWire[6];

    return 0;
}

void loadAndOfOrsCircuit(int* n, int* m, int* q, int* r, int* num_alice_inp) {
    *n = 4;
    *m = 1;
    *q = 3;
    *r = *q + *n;
    *num_alice_inp = 2;
}

void loadOrOfAndsCircuit(int* n, int* m, int* q, int* r, int* num_alice_inp) {
    *n = 8;
    *m = 1;
    *q = 7;
    *r = *q + *n;
    *num_alice_inp = 4;
}

int main(int argc, char **argv) {
	seedRandom();
    GarbledCircuit garbledCircuit;
	GarblingContext garblingContext;
    int n, m, q, r, num_alice_inp;

    //load parameters appropriate for a particular circuit
    if(atoi(argv[1])==1)
	    loadOrOfAndsCircuit(&n, &m, &q, &r, &num_alice_inp);
    else
        loadAndOfOrsCircuit(&n, &m, &q, &r, &num_alice_inp);

	//Setup input and output tokens/labels.
	block *labels = (block*) malloc(sizeof(block) * 2 * n);
	block *outputbs = (block*) malloc(sizeof(block) * m);
	int *inp = (int *) malloc(sizeof(int) * n);
	countToN(inp, n);
	int outputs[m];
	int i;

	OutputMap outputMap = outputbs;
	InputLabels inputLabels = labels;

	//Create a circuit.
	createEmptyGarbledCircuit(&garbledCircuit, n, m, q, r, inputLabels);
    printf("\n");
	startBuilding(&garbledCircuit, &garblingContext);
    if(atoi(argv[1])==1)
	    createOrOfAndsCircuit(&garbledCircuit, &garblingContext, n, inp, outputs);
    else
        createAndOfOrsCircuit(&garbledCircuit, &garblingContext, n, inp, outputs);
	finishBuilding(&garbledCircuit, &garblingContext, outputMap, outputs);
	garbleCircuit(&garbledCircuit, inputLabels, outputMap);

    //Generate random inputs
    int inputs[n];
    getRandomInputs(inputs, 0, num_alice_inp);

    printf("Random Alice inputs:");
    for(int i=0;i<num_alice_inp;i++)
        printf("%d ",inputs[i]);
    printf("\n");

    //Extract labels according to inputs
    block extractedLabels[n];
    extractLabels(extractedLabels, inputLabels, inputs, num_alice_inp);

//Alice is done

//Bob starts from here

    if(fork()==0) {
        block computedOutputMap[m];
        int outputVals[m], checkOutputs[m];
        int bobInps[n];
        getRandomInputs(bobInps, 0, n-num_alice_inp);
        extractBobLabels(extractedLabels, bobInps, num_alice_inp, n);
	    evaluate(&garbledCircuit, extractedLabels, computedOutputMap);
	    mapOutputs(outputMap, computedOutputMap, outputVals, m);
        printf("Random Bob inputs:");
        for(int i = 0; i < (n-num_alice_inp); i++)
            printf("%d ",bobInps[i]);
        printf("\nGC Output:%d\n",outputVals[0]);
    } else {
        sendBobLabels(inputLabels, num_alice_inp, n);
    }

	return 0;
}
