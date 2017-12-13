require "csv"
def convert(filename)
  d = CSV.read(filename + ".csv", {:col_sep => ", "})

  a = d.select {|e| e[2] == "Note_on_c"}.map {|e| [(e[1].to_f / 960).to_s + "f", e[4], e[5]]}
  
  File.open(filename + ".txt", "w") do |f|
    f.print a.map {|e| "Triple(" + e.join(", ") + ")" }.join(",\n")
  end
end

def convertTiming(filename)
  d = CSV.read(filename + ".csv", {:col_sep => ", "})

  a = d.select {|e| e[2] == "Note_on_c"}.map {|e| [(e[1].to_f / 960).to_s + "f", e[4], e[5]]}
  
  File.open(filename + ".txt", "w") do |f|
    f.print a.map {|e| e[0] }.join(", ")
  end
end

#convert("piano")
#convert("sax")
#convert("bass")
#convert("guitar1")
#convert("guitar2")
#convert("sax4")

#convert("guitar15")
#convert("guitar35")
#convert("bass5")
#convert("piano5")
#convertTiming("kick5")

#convert("sax6")
convert("bass6")
convert("guitar6")
convert("piano6")
convert("synth16")
convert("synth26")
