import wave, os, math, struct, random

OUT="app/src/main/res/raw"
SR=44100
os.makedirs(OUT, exist_ok=True)

def env(n,a=.01,r=.15):
    e=[1.0] * n
    aa=max(1,int(SR*a))
    rr=max(1,int(SR*r))
    for i in range(min(aa,n)):
        e[i]=i/aa
    for i in range(min(rr,n)):
        e[n-rr+i]*=1-i/rr
    return e

def tone(f,d,a=.5,bend=0):
    n=max(1,int(SR*d))
    phase=0.0
    out=[]
    envelope=env(n,.004,min(.25,d*.35))
    for i in range(n):
        freq=f*(1+bend*(i/SR)/max(d,.001))
        phase += 2*math.pi*freq/SR
        out.append(math.sin(phase)*a*envelope[i])
    return out

def noise(d,a=.05):
    n=max(1,int(SR*d))
    envelope=env(n,.002,min(.3,d*.4))
    return [random.gauss(0,1)*a*envelope[i] for i in range(n)]

def save(name,x):
    x=list(x)
    peak=max(1e-6,float(max(abs(v) for v in x)))
    x=[max(-1,min(1,v/peak*.92)) for v in x]
    with wave.open(os.path.join(OUT,name),"wb") as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(SR)
        f.writeframes(struct.pack("<" + "h"*len(x), *[int(v*32767) for v in x]))

# BRAIN
d=5.5; n=int(SR*d); x=[0.0] * n
for st,f,a,du in [(0,48,.9,.8),(1,72,.65,.5),(2,110,.5,.6),(3,180,.35,.8)]:
    i=int(st*SR); j=min(n,i+int(du*SR))
    x[i:j]+=tone(f,(j-i)/SR,a,2)
x+=noise(d,.12)+tone(42,d,.22)
save("gdmie_audio_brain.wav",x)

# COSMOS
d=12; t=[i/SR for i in range(int(SR*d))]
x=[.22*math.sin(2*math.pi*55*v) for v in t]
x=[v+.12*math.sin(2*math.pi*82.5*t[i]+.7*math.sin(t[i]*.4)) for i,v in enumerate(x)]
x=[v+.09*math.sin(2*math.pi*123.5*t[i]+1.2*math.sin(t[i]*.27)) for i,v in enumerate(x)]
x=[v*min(1,t[i]/.8)*min(1,(d-t[i])/1.5) for i,v in enumerate(x)]
save("gdmie_audio_cosmos.wav",x)

# ENERGY
d=10; n=int(SR*d); x=[0.0] * n
for k in range(20):
    i=int(k*.5*SR); j=min(n,i+int(.34*SR))
    x[i:j]+=tone(62*2**((k%4)/12),(j-i)/SR,.6,.15)
    x[i:j]+=tone(124,(j-i)/SR,.16)
save("gdmie_audio_energy.wav",x)

# SPACE
d=12; n=int(SR*d); t=[i/SR for i in range(n)]
x=[.25*math.sin(2*math.pi*36*v) for v in t]
x=[v+.10*math.sin(2*math.pi*73*t[i]+math.sin(t[i]*.5)) for i,v in enumerate(x)]
x=[v+.05*math.sin(2*math.pi*147*t[i]+math.sin(t[i]*.8)) for i,v in enumerate(x)]
for st,f in [(1.5,660),(4.2,990),(7.3,1320),(10.1,880)]:
    i=int(st*SR); j=min(n,i+int(.7*SR))
    x[i:j]+=tone(f,(j-i)/SR,.16)
x+=noise(d,.018)
save("gdmie_audio_space.wav",x)

# EARTH
d=10; n=int(SR*d); t=[i/SR for i in range(n)]
x=[.22*math.sin(2*math.pi*55*v)+.12*math.sin(2*math.pi*110*v) for v in t]
for k in range(10):
    i=int(k*SR); j=min(n,i+int(.5*SR))
    x[i:j]+=tone(220,(j-i)/SR,.16)
x+=noise(d,.025)
save("gdmie_audio_earth.wav",x)

# TIME
d=9; n=int(SR*d); t=[i/SR for i in range(n)]
rate=[2+.7*v for v in t]
fr_cumsum=0.0
phase=[]
for r in rate:
    fr_cumsum += r
    phase.append(2*math.pi*fr_cumsum/SR)
x=[.16*math.sin(phase[i])+.11*math.sin(2*phase[i]) for i in range(len(phase))]
x=[v+.08*math.sin(2*math.pi*(180+35*t[i]/d)*t[i]) for i,v in enumerate(x)]
x=[v*(.2+.8*i/(n-1)) for i,v in enumerate(x)]
save("gdmie_audio_time.wav",x)

# DECISION
d=8; n=int(SR*d); x=[0.0] * n
for k in range(16):
    i=int(k*.5*SR); j=min(n,i+int(.22*SR))
    x[i:j]+=tone(92 if k%2==0 else 138,(j-i)/SR,.42)
x+=tone(46,d,.12)
save("gdmie_audio_decision.wav",x)

# ANALYSIS
d=9; n=int(SR*d); t=[i/SR for i in range(n)]
x=[.08*math.sin(2*math.pi*(70+120*t[i]/d)*t[i])*min(1,t[i]/.8) for i in range(n)]
for f,a in [(261.63,.25),(329.63,.20),(392,.18),(523.25,.12)]:
    i=int(7*SR)
    x[i:]+=tone(f,(n-i)/SR,a,.02)
save("gdmie_audio_analysis.wav",x)

# XP
d=1.5; n=int(SR*d); x=[0.0] * n
for st,f in [(0,523.25),(.18,659.25),(.36,783.99),(.54,1046.5)]:
    i=int(st*SR); j=min(n,i+int(.42*SR))
    x[i:j]+=tone(f,(j-i)/SR,.65)
x+=noise(d,.025)
save("gdmie_audio_xp.wav",x)

# LEVEL UP
d=3.2; n=int(SR*d); x=[0.0] * n
for k,f in enumerate([261.63,329.63,392,523.25,659.25]):
    i=int(k*.32*SR); j=min(n,i+int(.8*SR))
    x[i:j]+=tone(f,(j-i)/SR,.45,.01)
x+=tone(65,d,.12)
save("gdmie_audio_levelup.wav",x)

print("GDMIE AUDIO GENERATION COMPLETE")
